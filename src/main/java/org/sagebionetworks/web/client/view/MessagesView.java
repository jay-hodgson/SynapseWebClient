package org.sagebionetworks.web.client.view;

import java.util.List;

import org.sagebionetworks.repo.model.message.MessageRecipientSet;
import org.sagebionetworks.repo.model.message.MessageStatusType;
import org.sagebionetworks.repo.model.message.MessageToUser;
import org.sagebionetworks.web.client.SynapsePresenter;
import org.sagebionetworks.web.client.SynapseView;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.IsWidget;

public interface MessagesView extends IsWidget, SynapseView {
	
	/**
	 * Set this view's presenter
	 * @param presenter
	 */
	void setPresenter(Presenter presenter);
	void showInbox();
	void showOutbox();
	
	public interface Presenter extends SynapsePresenter {
		void goTo(Place place);
		void clearSelectedMessages();
		void setSelectedMessages(List<String> messageIds);
		//use to change to read/unread/archive (website will archive when messages are deleted).
		void updateSelectedMessagesStatus(MessageStatusType status);
		
		void forwardSelectedMessage(MessageRecipientSet recipients);
		void sendMessage(MessageRecipientSet recipients, String subject, String body, String replyToMessageId);
		void refresh();
	}
}
