<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/webpage/include/taglib.jsp"%>
<html>
<head>
	<title>运费管理管理</title>
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
		<h5>运费管理列表 </h5>
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
	<sys:message content="${message}" hideType="1"/>
	
	<!--查询条件-->
	<div class="row">
	<div class="col-sm-12">
	<form:form id="searchForm" modelAttribute="goodsYunfeishezhi" action="${ctx}/goodsyunfeishezhi/goodsYunfeishezhi/" method="post" class="form-inline">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<table:sortColumn id="orderBy" name="orderBy" value="${page.orderBy}" callback="sortOrRefresh();"/><!-- 支持排序 -->
		<div class="form-group">
		 </div>	
	</form:form>
	<br/>
	</div>
	</div>
	
	<!-- 工具栏 -->
	<div class="row">
	<div class="col-sm-12">
		<div class="pull-left">
			<shiro:hasPermission name="goodsyunfeishezhi:goodsYunfeishezhi:add">
				<table:addRow url="${ctx}/goodsyunfeishezhi/goodsYunfeishezhi/form" title="运费管理" width="440px" height="175px"></table:addRow><!-- 增加按钮 -->
			</shiro:hasPermission>
			<shiro:hasPermission name="goodsyunfeishezhi:goodsYunfeishezhi:edit">
			    <table:editRow url="${ctx}/goodsyunfeishezhi/goodsYunfeishezhi/form" title="运费管理" id="contentTable" width="440px" height="175px"></table:editRow><!-- 编辑按钮 -->
			</shiro:hasPermission>
			<shiro:hasPermission name="goodsyunfeishezhi:goodsYunfeishezhi:del">
				<table:delRow url="${ctx}/goodsyunfeishezhi/goodsYunfeishezhi/deleteAll" id="contentTable"></table:delRow><!-- 删除按钮 -->
			</shiro:hasPermission>
			<shiro:hasPermission name="goodsyunfeishezhi:goodsYunfeishezhi:import">
				<table:importExcel url="${ctx}/goodsyunfeishezhi/goodsYunfeishezhi/import"></table:importExcel><!-- 导入按钮 -->
			</shiro:hasPermission>
			<shiro:hasPermission name="goodsyunfeishezhi:goodsYunfeishezhi:export">
	       		<table:exportExcel url="${ctx}/goodsyunfeishezhi/goodsYunfeishezhi/export"></table:exportExcel><!-- 导出按钮 -->
	       	</shiro:hasPermission>
	       <button class="btn btn-primary btn-outline btn-sm" data-toggle="tooltip" data-placement="left" onclick="sortOrRefresh()" title="刷新"><i class="glyphicon glyphicon-repeat"></i> 刷新</button>
		
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
				<th  class="sort-column yunfeishezhi">运费设置(元)</th>
				<th  class="sort-column updateDate">更新时间</th>
				<th  class="sort-column remarks">备注信息</th>
				<th>操作</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="goodsYunfeishezhi">
			<tr>
				<td> <input type="checkbox" id="${goodsYunfeishezhi.id}" class="i-checks"></td>
				<td><a  href="#" onclick="openDialogView('查看运费管理', '${ctx}/goodsyunfeishezhi/goodsYunfeishezhi/form?id=${goodsYunfeishezhi.id}','440px', '175px')">
					${goodsYunfeishezhi.yunfeishezhi}
				</a></td>
				<td>
					<fmt:formatDate value="${goodsYunfeishezhi.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					${goodsYunfeishezhi.remarks}
				</td>
				<td>
					<shiro:hasPermission name="goodsyunfeishezhi:goodsYunfeishezhi:view">
						<a href="#" onclick="openDialogView('查看运费管理', '${ctx}/goodsyunfeishezhi/goodsYunfeishezhi/form?id=${goodsYunfeishezhi.id}','440px', '175px')" class="btn btn-info btn-xs" ><i class="fa fa-search-plus"></i> 查看</a>
					</shiro:hasPermission>
					<shiro:hasPermission name="goodsyunfeishezhi:goodsYunfeishezhi:edit">
    					<a href="#" onclick="openDialog('修改运费管理', '${ctx}/goodsyunfeishezhi/goodsYunfeishezhi/form?id=${goodsYunfeishezhi.id}','440px', '175px')" class="btn btn-success btn-xs" ><i class="fa fa-edit"></i> 修改</a>
    				</shiro:hasPermission>
    				<shiro:hasPermission name="goodsyunfeishezhi:goodsYunfeishezhi:del">
						<a href="${ctx}/goodsyunfeishezhi/goodsYunfeishezhi/delete?id=${goodsYunfeishezhi.id}" onclick="return confirmx('确认要删除该运费管理吗？', this.href)"   class="btn btn-danger btn-xs"><i class="fa fa-trash"></i> 删除</a>
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