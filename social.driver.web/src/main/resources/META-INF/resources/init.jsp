<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %>
<%@ taglib uri="http://liferay.com/tld/frontend" prefix="liferay-frontend" %>
<%@ taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %>
<%@ taglib uri="http://liferay.com/tld/security" prefix="liferay-security" %>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>
<%@ taglib uri="http://liferay.com/tld/util" prefix="liferay-util" %>

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


<portlet:defineObjects/> 
<liferay-theme:defineObjects/> 
