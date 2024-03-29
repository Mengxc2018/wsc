<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/webpage/include/taglib.jsp"%>
<html>
<head>
	<title>分佣比例管理</title>
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
		<h5>分佣比例列表 </h5>
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
	<form:form id="searchForm" modelAttribute="dmFenyongbili" action="${ctx}/dmfenyongbili/dmFenyongbili/" method="post" class="form-inline">
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
			<shiro:hasPermission name="dmfenyongbili:dmFenyongbili:add">
				<table:addRow url="${ctx}/dmfenyongbili/dmFenyongbili/form" title="分佣比例" width="584.19px"></table:addRow><!-- 增加按钮 -->
			</shiro:hasPermission>
			<shiro:hasPermission name="dmfenyongbili:dmFenyongbili:edit">
			    <table:editRow url="${ctx}/dmfenyongbili/dmFenyongbili/form" title="分佣比例" id="contentTable" width="584.19px"></table:editRow><!-- 编辑按钮 -->
			</shiro:hasPermission>
			<shiro:hasPermission name="dmfenyongbili:dmFenyongbili:del">
				<table:delRow url="${ctx}/dmfenyongbili/dmFenyongbili/deleteAll" id="contentTable"></table:delRow><!-- 删除按钮 -->
			</shiro:hasPermission>
			<shiro:hasPermission name="dmfenyongbili:dmFenyongbili:import">
				<table:importExcel url="${ctx}/dmfenyongbili/dmFenyongbili/import"></table:importExcel><!-- 导入按钮 -->
			</shiro:hasPermission>
			<shiro:hasPermission name="dmfenyongbili:dmFenyongbili:export">
	       		<table:exportExcel url="${ctx}/dmfenyongbili/dmFenyongbili/export"></table:exportExcel><!-- 导出按钮 -->
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
				<th  class="sort-column wk1">1级微客分佣比例(%)</th>
				<th  class="sort-column wk2">2级微客分佣比例(%)</th>
				<th  class="sort-column jmf">加盟费（元）</th>
				<th  class="sort-column jm1">1级加盟商分佣比例(%)</th>
				<th  class="sort-column jm2">2级加盟商分佣比例(%)</th>
				<th  class="sort-column jmf11">加盟费1_1（元）</th>
				<th  class="sort-column jm11">1级加盟商分佣比例1_1(%)</th>
				<th  class="sort-column jm22">2级加盟商分佣比例2_2(%)</th>
				<th  class="sort-column jmtced">加盟提成额度(元)</th>
				<th  class="sort-column jmtcbl">加盟提成比例(%)</th>
				<th  class="sort-column jmtcbl">提现到账比例(%)</th>
				<th  class="sort-column remarks">备注信息</th>
				<th>操作</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="dmFenyongbili">
			<tr>
				<td> <input type="checkbox" id="${dmFenyongbili.id}" class="i-checks"></td>
				<td><a  href="#" onclick="openDialogView('查看分佣比例', '${ctx}/dmfenyongbili/dmFenyongbili/form?id=${dmFenyongbili.id}','800px', '500px')">
					${dmFenyongbili.wk1}
				</a></td>
				<td>
					${dmFenyongbili.wk2}
				</td>
				<td>
					${dmFenyongbili.jmf}
				</td>
				<td>
					${dmFenyongbili.jm1}
				</td>
				<td>
					${dmFenyongbili.jm2}
				</td>
				
				<td>
					${dmFenyongbili.jmf11}
				</td>
				<td>
					${dmFenyongbili.jm11}
				</td>
				<td>
					${dmFenyongbili.jm22}
				</td>
				
				<td>
					${dmFenyongbili.jmtced}
				</td>
				<td>
					${dmFenyongbili.jmtcbl}
				</td>
				<td>
					${dmFenyongbili.txdzbl}
				</td>
				<td>
					${dmFenyongbili.remarks}
				</td>
				<td>
					<shiro:hasPermission name="dmfenyongbili:dmFenyongbili:view">
						<a href="#" onclick="openDialogView('查看分佣比例', '${ctx}/dmfenyongbili/dmFenyongbili/form?id=${dmFenyongbili.id}','650px', '500px')" class="btn btn-info btn-xs" ><i class="fa fa-search-plus"></i> 查看</a>
					</shiro:hasPermission>
					<shiro:hasPermission name="dmfenyongbili:dmFenyongbili:edit">
    					<a href="#" onclick="openDialog('修改分佣比例', '${ctx}/dmfenyongbili/dmFenyongbili/form?id=${dmFenyongbili.id}','650px', '500px')" class="btn btn-success btn-xs" ><i class="fa fa-edit"></i> 修改</a>
    				</shiro:hasPermission>
    				<shiro:hasPermission name="dmfenyongbili:dmFenyongbili:del">
						<a href="${ctx}/dmfenyongbili/dmFenyongbili/delete?id=${dmFenyongbili.id}" onclick="return confirmx('确认要删除该分佣比例吗？', this.href)"   class="btn btn-danger btn-xs"><i class="fa fa-trash"></i> 删除</a>
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