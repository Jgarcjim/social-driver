
package com.liferay.social.driver.portlet;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.util.PortalUtil;
import com.liferay.social.driver.content.ContentGenerator;
import com.liferay.social.driver.thread.CreateContent;
import com.liferay.social.driver.thread.GenerateStats;
import com.liferay.util.bridges.mvc.MVCPortlet;

import java.io.IOException;

import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

public class SocialDriverPortlet extends MVCPortlet {

	private GenerateStats statsThread = null;
	private CreateContent createThread = null;
	private Boolean profileFlag = true;
	long companyId = 0;
	long groupId = 0;

	private final ContentGenerator contentContainer = new ContentGenerator();

	@Override
	public void destroy() {

		stopAll();
	}

	@Override
	public void serveResource(ResourceRequest request, ResourceResponse response)
		throws IOException {

		companyId = PortalUtil.getCompanyId(request);
		try {
			groupId = PortalUtil.getScopeGroupId(request);
		}
		catch (PortalException e) {
			e.printStackTrace();
		}
		catch (SystemException e) {
			e.printStackTrace();
		}

		String action = GetterUtil.getString(request.getParameter("action"));

		if (action.contains("startContent")) {
			startContent(request);
		}
		else if (action.contains("genStats")) {
			genStats();
		}
		else if (action.contains("stopAll")) {
			stopAll();
		}
	}

	public void startContent(ResourceRequest request) {

		String themeId = GetterUtil.getString(request.getParameter("themeId"));

		if (createThread == null) {
			stopAll();
			System.out.println("Starting Content Generator");
			createThread =
				new CreateContent(
					companyId, groupId, themeId, profileFlag, contentContainer);
			createThread.startThread();
		}
	}

	private void genStats() {

		if (statsThread == null) {
			stopAll();
			System.out.println("Starting Generating Social Stats");
			statsThread = new GenerateStats(groupId);
			statsThread.startThread();
		}
	}

	private void stopAll() {

		if (createThread != null) {
			System.out.println("Stopping All Generators");
			createThread.stopThread();
			createThread = null;
		}
		else if (statsThread != null) {
			System.out.println("Stopping All Generators");
			statsThread.stopThread();
			statsThread = null;
		}
	}
}
