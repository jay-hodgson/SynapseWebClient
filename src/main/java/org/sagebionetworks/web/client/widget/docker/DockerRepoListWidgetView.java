package org.sagebionetworks.web.client.widget.docker;

import org.sagebionetworks.repo.model.docker.DockerRepository;
import org.sagebionetworks.web.client.widget.LoadMoreWidgetContainer;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

public interface DockerRepoListWidgetView extends IsWidget {

	public interface Presenter {
		void onClickAddExternalRepo();
	}

	void setAddExternalRepoButtonVisible(boolean visibile);

	void addExternalRepoModal(IsWidget addExternalRepoModel);

	void setPresenter(Presenter presenter);

	void setSynAlert(Widget widget);

	void setSynAlertVisible(boolean visible);

	void clear();

	void addRepo(DockerRepository entity);

	void setMembersContainer(LoadMoreWidgetContainer membersContainer);
}
