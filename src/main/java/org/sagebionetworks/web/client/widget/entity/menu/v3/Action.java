package org.sagebionetworks.web.client.widget.entity.menu.v3;

/**
 * The enumeration of possible actions of an entity action menu.
 *
 * @author John
 *
 */
public enum Action {
  // Table specific
  SHOW_TABLE_SCHEMA,
  SHOW_VIEW_SCOPE,
  EDIT_ENTITYREF_COLLECTION_ITEMS,
  UPLOAD_TABLE_DATA,
  EDIT_TABLE_DATA,
  TOGGLE_FULL_TEXT_SEARCH,
  EDIT_DEFINING_SQL,
  VIEW_DEFINING_SQL,
  // All entity
  VIEW_SHARING_SETTINGS,
  CHANGE_ENTITY_NAME,
  EDIT_PROJECT_METADATA,
  EDIT_FILE_METADATA,
  CHANGE_STORAGE_LOCATION,
  SUBMIT_TO_CHALLENGE,
  MOVE_ENTITY,
  DELETE_ENTITY,
  EDIT_WIKI_PAGE,
  DELETE_WIKI_PAGE,
  VIEW_WIKI_SOURCE,
  ADD_WIKI_SUBPAGE,
  REORDER_WIKI_SUBPAGES,
  CREATE_LINK,
  UPLOAD_NEW_FILE,
  EDIT_PROVENANCE,
  CREATE_OR_UPDATE_DOI,
  ADD_EVALUATION_QUEUE,
  CREATE_CHALLENGE,
  DELETE_CHALLENGE,
  APPROVE_USER_ACCESS,
  PROJECT_DISPLAY,
  MANAGE_ACCESS_REQUIREMENTS,
  SHOW_ANNOTATIONS,
  SHOW_VERSION_HISTORY,
  UPLOAD_FILE,
  CREATE_FOLDER,
  UPLOAD_TABLE,
  ADD_TABLE,
  ADD_DATASET,
  ADD_DATASET_COLLECTION,
  FOLLOW,
  CREATE_THREAD,
  SHOW_DELETED_THREADS,
  EDIT_THREAD,
  PIN_THREAD,
  DELETE_THREAD,
  RESTORE_THREAD,
  CREATE_EXTERNAL_DOCKER_REPO,
  CREATE_TABLE_VERSION,
  SHOW_PROJECT_STATS,
  REPORT_VIOLATION,
  DOWNLOAD_FILE,
  ADD_TO_DOWNLOAD_CART,
  SHOW_PROGRAMMATIC_OPTIONS,
  PROJECT_HELP,
}
