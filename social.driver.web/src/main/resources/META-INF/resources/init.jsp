<%@ page import="com.liferay.portal.kernel.model.User" %>
<%@ page import="com.liferay.social.driver.web.constants.SocialDriverConstants" %>
<%@ page import="com.liferay.portal.kernel.service.UserLocalServiceUtil" %>
<%@ page import="com.liferay.message.boards.kernel.model.MBThread" %>
<%@ page import="com.liferay.message.boards.kernel.service.MBStatsUserLocalServiceUtil" %>
<%@ page import="com.liferay.message.boards.kernel.service.MBThreadLocalServiceUtil" %>
<%@ page import="com.liferay.wiki.model.WikiPage" %>
<%@ page import="com.liferay.wiki.model.WikiPageConstants" %>
<%@ page import="com.liferay.wiki.service.WikiPageLocalServiceUtil" %>
<%@ page import="java.net.URL" %>
<%@ page import="java.util.List" %>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet" %>
<%@ taglib prefix="liferay-ui" uri="http://liferay.com/tld/theme" %>
<%@ taglib prefix="liferay-theme" uri="http://liferay.com/tld/theme" %>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %>
<%@ taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %>

<portlet:defineObjects/> 
<liferay-theme:defineObjects/> 
<liferay-ui:defineObjects/>