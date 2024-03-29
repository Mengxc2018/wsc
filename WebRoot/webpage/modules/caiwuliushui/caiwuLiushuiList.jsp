<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/webpage/include/taglib.jsp"%>
<html>
<head>
	<title>财务流水</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
		});
	</script>
</head>
<body class="gray-bg">
	<div class="wrapper wrapper-content">
	<div class="ibox">
	<div class="ibox-title">
		<h5>财务流水列表 </h5>
		<div class="ibox-tools">
			<a class="collapse-link">
				<i class="fa fa-chevron-up"></i>
			</a>
			<a class="dropdown-toggle" data-toggle="dropdown" href="#">
				<i class="fa fa-wrench"></i>
			</a>
			<ul class="dropdown-menu dropdown-user">
				<li><a href="#">选项1</a>
				</li>
				<li><a href="#">选项2</a>
				</li>
			</ul>
			<a class="close-link">
				<i class="fa fa-times"></i>
			</a>
		</div>
	</div>
    
    <div class="ibox-content">
	<sys:message content="${message}"/>
	
	<!--查询条件-->
	<div class="row">
	<div class="col-sm-12">
	<form:form id="searchForm" modelAttribute="caiwuLiushui" action="${ctx}/caiwuliushui/caiwuLiushui/" method="post" class="form-inline">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<table:sortColumn id="orderBy" name="orderBy" value="${page.orderBy}" callback="sortOrRefresh();"/><!-- 支持排序 -->
		<div class="form-group">
			<span>交易类型：</span>
				<form:input path="jylx" htmlEscape="false" maxlength="64"  class=" form-control input-sm"/>
			<span>付款方：</span>
				<form:input path="fukuanfang" htmlEscape="false" maxlength="64"  class=" form-control input-sm"/>
			<span>收款方：</span>
				<form:input path="shoukuanfang" htmlEscape="false" maxlength="64"  class=" form-control input-sm"/>
		 </div>	
	</form:form>
	<br/>
	</div>
	</div>
	
	<!-- 工具栏 -->
	<div class="row">
	<div class="col-sm-12">
		<div class="pull-left">
			<shiro:hasPermission name="caiwuliushui:caiwuLiushui:add">
				<table:addRow url="${ctx}/caiwuliushui/caiwuLiushui/form" title="财务流水" width="507px" height="409px"></table:addRow><!-- 增加按钮 -->
			</shiro:hasPermission>
			<shiro:hasPermission name="caiwuliushui:caiwuLiushui:edit">
			    <table:editRow url="${ctx}/caiwuliushui/caiwuLiushui/form" title="财务流水" id="contentTable" width="507px" height="409px"></table:editRow><!-- 编辑按钮 -->
			</shiro:hasPermission>
			<shiro:hasPermission name="caiwuliushui:caiwuLiushui:del">
				<table:delRow url="${ctx}/caiwuliushui/caiwuLiushui/deleteAll" id="contentTable"></table:delRow><!-- 删除按钮 -->
			</shiro:hasPermission>
			<shiro:hasPermission name="caiwuliushui:caiwuLiushui:import">
				<table:importExcel url="${ctx}/caiwuliushui/caiwuLiushui/import"></table:importExcel><!-- 导入按钮 -->
			</shiro:hasPermission>
			<shiro:hasPermission name="caiwuliushui:caiwuLiushui:export">
	       		<table:exportExcel url="${ctx}/caiwuliushui/caiwuLiushui/export"></table:exportExcel><!-- 导出按钮 -->
	       	</shiro:hasPermission>
	       <button class="btn btn-primary btn-outline btn-sm " data-toggle="tooltip" data-placement="left" onclick="sortOrRefresh()" title="刷新"><i class="glyphicon glyphicon-repeat"></i> 刷新</button>
		
			</div>
		<div class="pull-right">
			<button  class="btn btn-primary btn-rounded btn-outline btn-sm " onclick="search()" ><i class="fa fa-search"></i> 查询</button>
			<button  class="btn btn-primary btn-rounded btn-outline btn-sm " onclick="reset()" ><i class="fa fa-refresh"></i> 重置</button>
		</div>
	</div>
	</div>
	
	<!-- 表格 -->
	<table id="contentTable" class="table table-striped table-bordered table-hover table-condensed dataTables-example dataTable">
		<thead>
			<tr>
				<th> <input type="checkbox" class="i-checks"></th>
				<th  class="sort-column jylx">交易类型</th>
				<th  class="sort-column fukuanfang">付款方</th>
				<th  class="sort-column shoukuanfang">收款方</th>
				<th  class="sort-column jyje">交易金额</th>
				<th  class="sort-column orderid">订单ID</th>
				<th  class="sort-column updateDate">更新时间</th>
				<th  class="sort-column remarks">备注信息</th>
				<th>操作</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="caiwuLiushui">
			<tr>
				<td> <input type="checkbox" id="${caiwuLiushui.id}" class="i-checks"></td>
				<td><a  href="#" onclick="openDialogView('查看财务流水', '${ctx}/caiwuliushui/caiwuLiushui/form?id=${caiwuLiushui.id}','507px', '409px')">
					${caiwuLiushui.jylx}
				</a></td>
				<td>
					${caiwuLiushui.fukuanfang}
				</td>
				<td>
					${caiwuLiushui.shoukuanfang}
				</td>
				<td>
					${caiwuLiushui.jyje}
				</td>
				<td>
					${caiwuLiushui.orderid}
				</td>
				<td>
					<fmt:formatDate value="${caiwuLiushui.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					${caiwuLiushui.remarks}
				</td>
				<td>
					<shiro:hasPermission name="caiwuliushui:caiwuLiushui:view">
						<a href="#" onclick="openDialogView('查看财务流水', '${ctx}/caiwuliushui/caiwuLiushui/form?id=${caiwuLiushui.id}','507px', '409px')" class="btn btn-info btn-xs" ><i class="fa fa-search-plus"></i> 查看</a>
					</shiro:hasPermission>
					<shiro:hasPermission name="caiwuliushui:caiwuLiushui:edit">
    					<a href="#" onclick="openDialog('修改财务流水', '${ctx}/caiwuliushui/caiwuLiushui/form?id=${caiwuLiushui.id}','507px', '409px')" class="btn btn-success btn-xs" ><i class="fa fa-edit"></i> 修改</a>
    				</shiro:hasPermission>
    				<shiro:hasPermission name="caiwuliushui:caiwuLiushui:del">
						<a href="${ctx}/caiwuliushui/caiwuLiushui/delete?id=${caiwuLiushui.id}" onclick="return confirmx('确认要删除该财务流水吗？', this.href)"   class="btn btn-danger btn-xs"><i class="fa fa-trash"></i> 删除</a>
					</shiro:hasPermission>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	
		<!-- 分页代码 -->
	<table:page page="${page}"></table:page>
	<br/>
	<br/>
	</div>
	</div>
</div>
</body>
</html>