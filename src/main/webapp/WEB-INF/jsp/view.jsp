<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.osivia.org/jsp/taglib/osivia-portal" prefix="op"%>

<portlet:defineObjects />

<portlet:actionURL name="newDoc" var="newDocUrl">
	<portlet:param name="docType" value="${docType}"/>
</portlet:actionURL>

<link rel="stylesheet" href="${pageContext.request.contextPath}/css/document-creation.css" />

<div class="document-creation-header">
	<button class="close" type="button" data-dismiss="modal" aria-label='<op:translate key="CLOSE"/>'>
    	<span aria-hidden="true">&times;</span>
    </button>
                
	<h4 class="modal-title">
      <i class="${docIcon}"></i>
      <span><op:translate key="${modalTitle}"/></span>
    </h4>
</div>

<c:if test="${not empty error}">
	<div class="alert alert-danger alert-dismissible" role="alert">
		<button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>
		${error}
	</div>
</c:if>

<form:form action="${newDocUrl}">
	<div class="form-group">
		<div class="input-group">
			<input class="form-control" name="newDocName" placeholder='<op:translate key="NEW_DOC_TITLE"/>' type="text">
			<div class="input-group-addon">${docExt}</div>
		</div>
	</div>
	<button class="btn btn-primary" type="submit"><op:translate key="NEW_DOC_CREATE"/></button>
	<button class="btn btn-default pull-right" type="button" data-dismiss="modal"><op:translate key="CANCEL"/></button>
</form:form>