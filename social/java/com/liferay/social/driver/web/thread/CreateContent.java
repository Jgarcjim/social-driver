
package com.liferay.social.driver.web.thread;

import com.liferay.social.driver.web.constants.SocialDriverConstants;
import com.liferay.social.driver.web.content.BlogManager;
import com.liferay.social.driver.web.content.ContentGenerator;
import com.liferay.social.driver.web.content.MBManager;
import com.liferay.social.driver.web.content.WikiManager;

public class CreateContent extends Thread {

	private Boolean running = false;
	long companyId;
	long groupId;
	String themeId;
	ContentGenerator contentContainer;

	public CreateContent(
		long companyId, long groupId, String themeId,
		ContentGenerator contentContainer) {

		this.companyId = companyId;
		this.groupId = groupId;
		this.themeId = themeId;
		this.contentContainer = contentContainer;
		
		new BlogManager(
			companyId, groupId, themeId, contentContainer);

		new MBManager(
			companyId, groupId, themeId, contentContainer);
		
		new WikiManager(
			companyId, groupId, themeId, contentContainer);


	}

	private SocialDriverConstants.CONTENT_ACTION getRandomAction() {

		return SocialDriverConstants.CONTENT_ACTION.values()[(int) (Math.random() * ((double) SocialDriverConstants.CONTENT_ACTION.values().length))];
	}

	public void run() {

		try {
			while (running) {

				switch (getRandomAction()) {

				case ADD_BLOG_ENTRY:
					BlogManager.addEntry();
					break;
				case ADD_BLOG_COMMENT:
					BlogManager.commentEntry();
					break;
				case VOTE_BLOG_ENTRY:
					BlogManager.voteEntry();
					break;
				case ADD_MB_MESSAGE:
					MBManager.addMsg();
					break;
				case ADD_MB_REPLY:
					MBManager.replyMsg();
					break;
				case ADD_WIKI_ENTRY:
					WikiManager.addEntry();
					break;
				case ADD_WIKI_COMMENT:
					WikiManager.commentEntry();
					break;
				case VOTE_WIKI_ENTRY:
					WikiManager.voteEntry();
					break;
				default:
					break;
				}
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void startThread() {

		running = true;
		start();
	}

	public void stopThread() {

		running = false;
	}

	public boolean isRunning() {

		return running;
	}
}
