/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.hserrorlog.web;

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
import com.jeeplus.modules.hserrorlog.entity.HsErrorLog;
import com.jeeplus.modules.hserrorlog.service.HsErrorLogService;

/**
 * 系统日志Controller
 * @author mxc
 * @version 2017-05-28
 */
@Controller
@RequestMapping(value = "${adminPath}/hserrorlog/hsErrorLog")
public class HsErrorLogController extends BaseController {

	@Autowired
	private HsErrorLogService hsErrorLogService;
	
	@ModelAttribute
	public HsErrorLog get(@RequestParam(required=false) String id) {
		HsErrorLog entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = hsErrorLogService.get(id);
		}
		if (entity == null){
			entity = new HsErrorLog();
		}
		return entity;
	}
	
	/**
	 * 系统日志列表页面
	 */
	@RequiresPermissions("hserrorlog:hsErrorLog:list")
	@RequestMapping(value = {"list", ""})
	public String list(HsErrorLog hsErrorLog, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<HsErrorLog> page = hsErrorLogService.findPage(new Page<HsErrorLog>(request, response), hsErrorLog); 
		model.addAttribute("page", page);
		return "modules/hserrorlog/hsErrorLogList";
	}

	/**
	 * 查看，增加，编辑系统日志表单页面
	 */
	@RequiresPermissions(value={"hserrorlog:hsErrorLog:view","hserrorlog:hsErrorLog:add","hserrorlog:hsErrorLog:edit"},logical=Logical.OR)
	@RequestMapping(value = "form")
	public String form(HsErrorLog hsErrorLog, Model model) {
		model.addAttribute("hsErrorLog", hsErrorLog);
		return "modules/hserrorlog/hsErrorLogForm";
	}

	/**
	 * 保存系统日志
	 */
	@RequiresPermissions(value={"hserrorlog:hsErrorLog:add","hserrorlog:hsErrorLog:edit"},logical=Logical.OR)
	@RequestMapping(value = "save")
	public String save(HsErrorLog hsErrorLog, Model model, RedirectAttributes redirectAttributes) throws Exception{
		if (!beanValidator(model, hsErrorLog)){
			return form(hsErrorLog, model);
		}
		if(!hsErrorLog.getIsNewRecord()){//编辑表单保存
			HsErrorLog t = hsErrorLogService.get(hsErrorLog.getId());//从数据库取出记录的值
			MyBeanUtils.copyBeanNotNull2Bean(hsErrorLog, t);//将编辑表单中的非NULL值覆盖数据库记录中的值
			hsErrorLogService.save(t);//保存
		}else{//新增表单保存
			hsErrorLogService.save(hsErrorLog);//保存
		}
		addMessage(redirectAttributes, "保存系统日志成功");
		return "redirect:"+Global.getAdminPath()+"/hserrorlog/hsErrorLog/?repage";
	}
	
	/**
	 * 删除系统日志
	 */
	@RequiresPermissions("hserrorlog:hsErrorLog:del")
	@RequestMapping(value = "delete")
	public String delete(HsErrorLog hsErrorLog, RedirectAttributes redirectAttributes) {
		hsErrorLogService.delete(hsErrorLog);
		addMessage(redirectAttributes, "删除系统日志成功");
		return "redirect:"+Global.getAdminPath()+"/hserrorlog/hsErrorLog/?repage";
	}
	
	/**
	 * 批量删除系统日志
	 */
	@RequiresPermissions("hserrorlog:hsErrorLog:del")
	@RequestMapping(value = "deleteAll")
	public String deleteAll(String ids, RedirectAttributes redirectAttributes) {
		String idArray[] =ids.split(",");
		for(String id : idArray){
			hsErrorLogService.delete(hsErrorLogService.get(id));
		}
		addMessage(redirectAttributes, "删除系统日志成功");
		return "redirect:"+Global.getAdminPath()+"/hserrorlog/hsErrorLog/?repage";
	}
	
	/**
	 * 导出excel文件
	 */
	@RequiresPermissions("hserrorlog:hsErrorLog:export")
    @RequestMapping(value = "export", method=RequestMethod.POST)
    public String exportFile(HsErrorLog hsErrorLog, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "系统日志"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<HsErrorLog> page = hsErrorLogService.findPage(new Page<HsErrorLog>(request, response, -1), hsErrorLog);
    		new ExportExcel("系统日志", HsErrorLog.class).setDataList(page.getList()).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导出系统日志记录失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/hserrorlog/hsErrorLog/?repage";
    }

	/**
	 * 导入Excel数据

	 */
	@RequiresPermissions("hserrorlog:hsErrorLog:import")
    @RequestMapping(value = "import", method=RequestMethod.POST)
    public String importFile(MultipartFile file, RedirectAttributes redirectAttributes) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<HsErrorLog> list = ei.getDataList(HsErrorLog.class);
			for (HsErrorLog hsErrorLog : list){
				try{
					hsErrorLogService.save(hsErrorLog);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条系统日志记录。");
			}
			addMessage(redirectAttributes, "已成功导入 "+successNum+" 条系统日志记录"+failureMsg);
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入系统日志失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/hserrorlog/hsErrorLog/?repage";
    }
	
	/**
	 * 下载导入系统日志数据模板
	 */
	@RequiresPermissions("hserrorlog:hsErrorLog:import")
    @RequestMapping(value = "import/template")
    public String importFileTemplate(HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "系统日志数据导入模板.xlsx";
    		List<HsErrorLog> list = Lists.newArrayList(); 
    		new ExportExcel("系统日志数据", HsErrorLog.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入模板下载失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/hserrorlog/hsErrorLog/?repage";
    }
	
	
	

}