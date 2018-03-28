<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.osivia.org/jsp/taglib/osivia-portal" prefix="op"%>

<portlet:defineObjects />

<portlet:actionURL name="newDoc" var="newDocUrl">
	<portlet:param name="docType" value="${docType}"/>
</portlet:actionURL>

<div>
	<button class="close" type="button" data-dismiss="modal" aria-label='<op:translate key="CLOSE"/>'>
      <span aria-hidden="true">×</span>
    </button>
                
	<h4 class="modal-title">
      <i class="${docIcon}"></i>
      <span><op:translate key="${modalTitle}"/></span>
    </h4>
</div>

<form:form action="${newDocUrl}">
	<div class="form-group">
		<label for="newDocName"></label>
		<div class="input-group">
			<input class="form-control" name="newDocName" placeholder='<op:translate key="NEW_DOC_TITLE"/>' type="text">
			<div class="input-group-addon">${docExt}</div>
		</div>
	</div>
	<button class="btn btn-primary" type="submit"><op:translate key="NEW_DOC_CREATE"/></button>
	<button class="btn btn-default pull-right" type="button" data-dismiss="modal"><op:translate key="CANCEL"/></button>
</form:form>