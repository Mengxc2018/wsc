/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.fenxiaoyongjin.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Lists;
import com.jeeplus.common.utils.DateUtils;
import com.jeeplus.common.utils.MyBeanUtils;
import com.jeeplus.common.config.Global;
import com.jeeplus.common.persistence.Page;
import com.jeeplus.common.web.BaseController;
import com.jeeplus.common.utils.StringUtils;
import com.jeeplus.common.utils.excel.ExportExcel;
import com.jeeplus.common.utils.excel.ImportExcel;
import com.jeeplus.modules.fenxiaoyongjin.entity.FenxiaoYongjin;
import com.jeeplus.modules.fenxiaoyongjin.service.FenxiaoYongjinService;

/**
 * 分销佣金管理Controller
 * @author mxc
 * @version 2017-05-16
 */

/**
 * 请求比如是'/fenxiaoyongjin/fenxiaoYongjin/save',先匹配controller的requesetmapping,匹配到了FenxiaoYongjinController类，再
 * 匹配FenxiaoYongjinController类中的方法，方法的requesetmapping必须等于save
 * @author PG01
 *
 */

@Controller
@RequestMapping(value = "${adminPath}/fenxiaoyongjin/fenxiaoYongjin")
public class FenxiaoYongjinController extends BaseController {

	@Autowired
	private FenxiaoYongjinService fenxiaoYongjinService;
	
	/**
	 * 1. 走controller里的任何一个请求，都必须先走这个get方法(第一步)
	 * get方法的作用，如果数据库中包含对应id（页面传递过来的）的记录，则取出这个记录并赋值到entity,如果没有数据库的记录，则生成一个entity
	 * 这个return的entity,赋值到所有的controller方法参数中的entity。
	 * 
	 * 2.get方法执行完毕后，执行save方法之前，将页面传递的表单数据再赋值到entity（第二步），变成页面最新数据的entity.
	 * 
	 * 3. 开始执行save方法中的语句
	 * 
	 * @param id
	 * @return
	 */
	@ModelAttribute
	public FenxiaoYongjin get(@RequestParam(required=false) String id) {
		FenxiaoYongjin entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = fenxiaoYongjinService.get(id);
		}
		if (entity == null){
			entity = new FenxiaoYongjin();
		}
		return entity;
	}
	
	/**
	 * 分销佣金记录列表页面
	 */
	@RequiresPermissions("fenxiaoyongjin:fenxiaoYongjin:list")
	@RequestMapping(value = {"list", ""})
	public String list(FenxiaoYongjin fenxiaoYongjin, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<FenxiaoYongjin> page = fenxiaoYongjinService.findPage(new Page<FenxiaoYongjin>(request, response), fenxiaoYongjin); //分页查询，显示某页的多行记录，里边需要调用findList方法
		model.addAttribute("page", page);  
		return "modules/fenxiaoyongjin/fenxiaoYongjinList"; //返回到页面，webpage/modules/fenxiaoyongjin/fenxiaoYongjinList.jsp,页面中就能使用model中保存的属性了
	}

	/**
	 * 查看，增加，编辑分销佣金记录表单页面
	 */
	@RequiresPermissions(value={"fenxiaoyongjin:fenxiaoYongjin:view","fenxiaoyongjin:fenxiaoYongjin:add","fenxiaoyongjin:fenxiaoYongjin:edit"},logical=Logical.OR)
	@RequestMapping(value = "form")
	public String form(FenxiaoYongjin fenxiaoYongjin, Model model) {
		model.addAttribute("fenxiaoYongjin", fenxiaoYongjin);
		return "modules/fenxiaoyongjin/fenxiaoYongjinForm";
	}

	/**
	 * 保存分销佣金记录
	 */
	@RequiresPermissions(value={"fenxiaoyongjin:fenxiaoYongjin:add","fenxiaoyongjin:fenxiaoYongjin:edit"},logical=Logical.OR)
	@RequestMapping(value = "save")
	public String save(FenxiaoYongjin fenxiaoYongjin, Model model, RedirectAttributes redirectAttributes) throws Exception{
		if (!beanValidator(model, fenxiaoYongjin)){
			return form(fenxiaoYongjin, model);
		}
		if(!fenxiaoYongjin.getIsNewRecord()){//编辑表单保存
			FenxiaoYongjin t = fenxiaoYongjinService.get(fenxiaoYongjin.getId());//从数据库取出记录的值
			MyBeanUtils.copyBeanNotNull2Bean(fenxiaoYongjin, t);//将编辑表单中的非NULL值覆盖数据库记录中的值
			fenxiaoYongjinService.save(t);//保存
		}else{//新增表单保存
			fenxiaoYongjinService.save(fenxiaoYongjin);//保存
		}
		addMessage(redirectAttributes, "保存分销佣金记录成功");
		return "redirect:"+Global.getAdminPath()+"/fenxiaoyongjin/fenxiaoYongjin/?repage";
	}
	
	/**
	 * 删除分销佣金记录
	 */
	@RequiresPermissions("fenxiaoyongjin:fenxiaoYongjin:del")
	@RequestMapping(value = "delete")
	public String delete(FenxiaoYongjin fenxiaoYongjin, RedirectAttributes redirectAttributes) {
		fenxiaoYongjinService.delete(fenxiaoYongjin);
		addMessage(redirectAttributes, "删除分销佣金记录成功");
		return "redirect:"+Global.getAdminPath()+"/fenxiaoyongjin/fenxiaoYongjin/?repage";
	}
	
	/**
	 * 批量删除分销佣金记录
	 */
	@RequiresPermissions("fenxiaoyongjin:fenxiaoYongjin:del")
	@RequestMapping(value = "deleteAll")
	public String deleteAll(String ids, RedirectAttributes redirectAttributes) {
		String idArray[] =ids.split(",");
		for(String id : idArray){
			fenxiaoYongjinService.delete(fenxiaoYongjinService.get(id));
		}
		addMessage(redirectAttributes, "删除分销佣金记录成功");
		return "redirect:"+Global.getAdminPath()+"/fenxiaoyongjin/fenxiaoYongjin/?repage";
	}
	
	/**
	 * 导出excel文件
	 */
	@RequiresPermissions("fenxiaoyongjin:fenxiaoYongjin:export")
    @RequestMapping(value = "export", method=RequestMethod.POST)
    public String exportFile(FenxiaoYongjin fenxiaoYongjin, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "分销佣金记录"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<FenxiaoYongjin> page = fenxiaoYongjinService.findPage(new Page<FenxiaoYongjin>(request, response, -1), fenxiaoYongjin);
    		new ExportExcel("分销佣金记录", FenxiaoYongjin.class).setDataList(page.getList()).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导出分销佣金记录记录失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/fenxiaoyongjin/fenxiaoYongjin/?repage";
    }

	/**
	 * 导入Excel数据

	 */
	@RequiresPermissions("fenxiaoyongjin:fenxiaoYongjin:import")
    @RequestMapping(value = "import", method=RequestMethod.POST)
    public String importFile(MultipartFile file, RedirectAttributes redirectAttributes) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<FenxiaoYongjin> list = ei.getDataList(FenxiaoYongjin.class);
			for (FenxiaoYongjin fenxiaoYongjin : list){
				try{
					fenxiaoYongjinService.save(fenxiaoYongjin);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条分销佣金记录记录。");
			}
			addMessage(redirectAttributes, "已成功导入 "+successNum+" 条分销佣金记录记录"+failureMsg);
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入分销佣金记录失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/fenxiaoyongjin/fenxiaoYongjin/?repage";
    }
	
	/**
	 * 下载导入分销佣金记录数据模板
	 */
	@RequiresPermissions("fenxiaoyongjin:fenxiaoYongjin:import")
    @RequestMapping(value = "import/template")
    public String importFileTemplate(HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "分销佣金记录数据导入模板.xlsx";
    		List<FenxiaoYongjin> list = Lists.newArrayList(); 
    		new ExportExcel("分销佣金记录数据", FenxiaoYongjin.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入模板下载失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/fenxiaoyongjin/fenxiaoYongjin/?repage";
    }
	
	
	

}