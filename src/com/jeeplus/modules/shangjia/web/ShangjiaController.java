/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.shangjia.web;

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
import com.jeeplus.modules.shangjia.entity.Shangjia;
import com.jeeplus.modules.shangjia.service.ShangjiaService;

/**
 * 商家管理Controller
 * @author mxc
 * @version 2017-05-16
 */
@Controller
@RequestMapping(value = "${adminPath}/shangjia/shangjia")
public class ShangjiaController extends BaseController {

	@Autowired
	private ShangjiaService shangjiaService;
	
	@ModelAttribute
	public Shangjia get(@RequestParam(required=false) String id) {
		Shangjia entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = shangjiaService.get(id);
		}
		if (entity == null){
			entity = new Shangjia();
		}
		return entity;
	}
	
	/**
	 * 商家记录列表页面
	 */
	@RequiresPermissions("shangjia:shangjia:list")
	@RequestMapping(value = {"list", ""})
	public String list(Shangjia shangjia, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<Shangjia> page = shangjiaService.findPage(new Page<Shangjia>(request, response), shangjia); 
		model.addAttribute("page", page);
		return "modules/shangjia/shangjiaList";
	}

	/**
	 * 查看，增加，编辑商家记录表单页面
	 */
	@RequiresPermissions(value={"shangjia:shangjia:view","shangjia:shangjia:add","shangjia:shangjia:edit"},logical=Logical.OR)
	@RequestMapping(value = "form")
	public String form(Shangjia shangjia, Model model) {
		List<Shangjia> list = shangjiaService.findList(new Shangjia());
		if(list.size() > 0){
			shangjia = list.get(0);
		}
		model.addAttribute("shangjia", shangjia);
		return "modules/shangjia/shangjiaForm";
	}

	/**
	 * 保存商家记录
	 */
	@RequiresPermissions(value={"shangjia:shangjia:add","shangjia:shangjia:edit"},logical=Logical.OR)
	@RequestMapping(value = "save")
	public String save(Shangjia shangjia, Model model, RedirectAttributes redirectAttributes) throws Exception{
		if (!beanValidator(model, shangjia)){
			return form(shangjia, model);
		}
		if(!shangjia.getIsNewRecord()){//编辑表单保存
			Shangjia t = shangjiaService.get(shangjia.getId());//从数据库取出记录的值
			MyBeanUtils.copyBeanNotNull2Bean(shangjia, t);//将编辑表单中的非NULL值覆盖数据库记录中的值
			shangjiaService.save(t);//保存
		}else{//新增表单保存
			shangjiaService.save(shangjia);//保存
		}
		addMessage(redirectAttributes, "保存商家记录成功");
		return "redirect:"+Global.getAdminPath()+"/shangjia/shangjia/?repage";
	}
	
	/**
	 * 删除商家记录
	 */
	@RequiresPermissions("shangjia:shangjia:del")
	@RequestMapping(value = "delete")
	public String delete(Shangjia shangjia, RedirectAttributes redirectAttributes) {
		shangjiaService.delete(shangjia);
		addMessage(redirectAttributes, "删除商家记录成功");
		return "redirect:"+Global.getAdminPath()+"/shangjia/shangjia/?repage";
	}
	
	/**
	 * 批量删除商家记录
	 */
	@RequiresPermissions("shangjia:shangjia:del")
	@RequestMapping(value = "deleteAll")
	public String deleteAll(String ids, RedirectAttributes redirectAttributes) {
		String idArray[] =ids.split(",");
		for(String id : idArray){
			shangjiaService.delete(shangjiaService.get(id));
		}
		addMessage(redirectAttributes, "删除商家记录成功");
		return "redirect:"+Global.getAdminPath()+"/shangjia/shangjia/?repage";
	}
	
	/**
	 * 导出excel文件
	 */
	@RequiresPermissions("shangjia:shangjia:export")
    @RequestMapping(value = "export", method=RequestMethod.POST)
    public String exportFile(Shangjia shangjia, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "商家记录"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<Shangjia> page = shangjiaService.findPage(new Page<Shangjia>(request, response, -1), shangjia);
    		new ExportExcel("商家记录", Shangjia.class).setDataList(page.getList()).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导出商家记录记录失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/shangjia/shangjia/?repage";
    }

	/**
	 * 导入Excel数据

	 */
	@RequiresPermissions("shangjia:shangjia:import")
    @RequestMapping(value = "import", method=RequestMethod.POST)
    public String importFile(MultipartFile file, RedirectAttributes redirectAttributes) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<Shangjia> list = ei.getDataList(Shangjia.class);
			for (Shangjia shangjia : list){
				try{
					shangjiaService.save(shangjia);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条商家记录记录。");
			}
			addMessage(redirectAttributes, "已成功导入 "+successNum+" 条商家记录记录"+failureMsg);
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入商家记录失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/shangjia/shangjia/?repage";
    }
	
	/**
	 * 下载导入商家记录数据模板
	 */
	@RequiresPermissions("shangjia:shangjia:import")
    @RequestMapping(value = "import/template")
    public String importFileTemplate(HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "商家记录数据导入模板.xlsx";
    		List<Shangjia> list = Lists.newArrayList(); 
    		new ExportExcel("商家记录数据", Shangjia.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入模板下载失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/shangjia/shangjia/?repage";
    }
	
	
	

}