import { test as base, Browser, expect, type Page } from '@playwright/test'
import { unlinkSync } from 'fs'
import { baseURL, defaultTestTimeout } from '../../playwright.config'
import { setCertifiedUserStatus } from '../helpers/certification'
import {
  cleanupTestUser,
  createTestUser,
  getAdminPAT,
  getUserIdFromLocalStorage,
  loginTestUser,
} from '../helpers/testUser'
import {
  storageStateDir,
  userConfigs,
  userPrefix,
  UserPrefix,
  userValidatedPrefix,
} from '../helpers/userConfig'
import { waitForInitialPageLoad } from '../helpers/utils'

type AuthenticatedUserPageTestFixtures = {
  userPage: Page
  validatedUserPage: Page
}

export type StorageStatePaths = { [key in UserPrefix]?: string }
type AuthenticatedUserPageWorkerFixtures = {
  createUsers: void
  storageStatePaths: StorageStatePaths
}

const createPage = async (browser: Browser) => {
  const page = await browser.newPage()
  // specify baseURL so we call this method outside of a test
  // ...i.e. where the baseURL test fixture is not available
  await page.goto(baseURL)
  await waitForInitialPageLoad(page)
  return page
}

// Generate test fixture for a specified user
// ...based on: https://github.com/microsoft/playwright/issues/14570
function createUserPageFixture<Page>(userPrefix: UserPrefix) {
  return async (
    { browser, storageStatePaths },
    use: (r: Page) => Promise<void>,
  ) => {
    const context = await browser.newContext({
      storageState: storageStatePaths[userPrefix],
    })
    const userPage = await context.newPage()

    await use(userPage)
    await context.close()
  }
}

export const testAuth = base.extend<
  AuthenticatedUserPageTestFixtures,
  AuthenticatedUserPageWorkerFixtures
>({
  // creates and deletes all test users defined in userConfig
  // ...per worker + browserName
  createUsers: [
    async ({ browser }, use) => {
      let userIds: string[] = []

      const page = await base.step('setup page', async () => {
        return await createPage(browser)
      })

      for (const user of Object.values(userConfigs)) {
        const userId = await base.step(
          `create test user - ${user.username}`,
          async () => {
            const userId = await createTestUser(user, getAdminPAT(), page)
            expect(userId).not.toBeUndefined()
            userIds.push(userId)
            return userId
          },
        )

        await base.step(`certify test user - ${user.username}`, async () => {
          await setCertifiedUserStatus(userId, true, getAdminPAT(), page)
        })
      }

      // close page so it isn't open while running all tests that depend on this worker fixture
      await base.step('close page', async () => {
        await page.close()
      })

      await use()

      const cleanupPage = await createPage(browser)
      for (const userId of userIds) {
        await cleanupTestUser(userId, cleanupPage)
      }
      await cleanupPage.close()
    },
    // specify timeout so worker fixture duration doesn't skew test duration
    // ...see https://github.com/microsoft/playwright/issues/15338
    { scope: 'worker', timeout: defaultTestTimeout },
  ],
  // stores auth state for each user in userConfig in storageState file, then deletes file
  // ...per worker + browserName
  // ...depends on createUsers worker fixture to create each test user
  storageStatePaths: [
    async ({ browser, browserName, createUsers }, use) => {
      let storageStatePaths: StorageStatePaths = {}

      for (const [userPrefix, user] of Object.entries(userConfigs)) {
        const userPage = await base.step(
          `create page: ${user.username}`,
          async () => {
            return await createPage(browser)
          },
        )

        await base.step(`authenticate: ${user.username}`, async () => {
          await loginTestUser(userPage, user.username, user.password)

          // ensure that session_marker, i.e. user id, is set before saving state
          await expect(async () => {
            await getUserIdFromLocalStorage(userPage)
          }).toPass()
        })

        await base.step(`save storageState: ${user.username}`, async () => {
          const storageStateFileName = `${browserName}_${process.env.TEST_PARALLEL_INDEX}_${user.username}.json`
          const storageStatePath = `${storageStateDir}/${storageStateFileName}`
          await userPage.context().storageState({ path: storageStatePath })
          storageStatePaths[userPrefix] = storageStatePath
          await userPage.close()
        })
      }

      await use(storageStatePaths)

      // delete files
      for (const userPrefix in storageStatePaths) {
        unlinkSync(storageStatePaths[userPrefix])
      }
    },
    // specify timeout so worker fixture duration doesn't skew test duration
    // ...see https://github.com/microsoft/playwright/issues/15338
    { scope: 'worker', timeout: defaultTestTimeout },
  ],
  userPage: createUserPageFixture(userPrefix),
  validatedUserPage: createUserPageFixture(userValidatedPrefix),
})
