/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.goods.web;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
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

import com.jeeplus.modules.goodscategory.entity.GoodsCategory;
import com.google.common.collect.Lists;
import com.jeeplus.common.utils.DateUtils;
import com.jeeplus.common.utils.MyBeanUtils;
import com.jeeplus.common.config.Global;
import com.jeeplus.common.persistence.Page;
import com.jeeplus.common.web.BaseController;
import com.jeeplus.common.utils.StringUtils;
import com.jeeplus.common.utils.excel.ExportExcel;
import com.jeeplus.common.utils.excel.ImportExcel;
import com.jeeplus.modules.goods.entity.Goods;
import com.jeeplus.modules.goods.service.GoodsService;

/**
 * 商品Controller
 * @author mxc
 * @version 2017-05-11
 */
@Controller
@RequestMapping(value = "${adminPath}/goods/goods")
public class GoodsController extends BaseController {

	@Autowired
	private GoodsService goodsService;
	
	@ModelAttribute
	public Goods get(@RequestParam(required=false) String id) {
		Goods entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = goodsService.get(id);
		}
		if (entity == null){
			entity = new Goods();
		}
		return entity;
	}
	
	/**
	 * 商品列表页面
	 */
	@RequiresPermissions("goods:goods:list")
	@RequestMapping(value = {"list", ""})
	public String list(Goods goods, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<Goods> page = goodsService.findPage(new Page<Goods>(request, response), goods); 
		model.addAttribute("page", page);
		return "modules/goods/goodsList";
	}

	/**
	 * 查看，增加，编辑商品表单页面
	 */
	@RequiresPermissions(value={"goods:goods:view","goods:goods:add","goods:goods:edit"},logical=Logical.OR)
	@RequestMapping(value = "form")
	public String form(Goods goods, Model model) {
		//排序自动附最大值
		if(goods.getSort() == null){
			List<Goods> lists = goodsService.findList(goods);
			int j = 0;
			if(lists.size()!=0){
				for(int i = 0; i < lists.size(); i++){
					if(lists.get(i).getSort() > j){
						j = lists.get(i).getSort();
						goods.setSort(j+1);
						goods.setXgnum(0);
					}
				}
			}else{
				goods.setSort(j+1);
				goods.setXgnum(0);
			}
		}
		model.addAttribute("goods", goods);
		return "modules/goods/goodsForm";
	}

	/**
	 * 保存商品
	 */
	@RequiresPermissions(value={"goods:goods:add","goods:goods:edit"},logical=Logical.OR)
	@RequestMapping(value = "save")
	public String save(Goods goods, Model model, RedirectAttributes redirectAttributes) throws Exception{
		if (!beanValidator(model, goods)){
			return form(goods, model);
		}
		if(!goods.getIsNewRecord()){//编辑表单保存
			Goods t = goodsService.get(goods.getId());//从数据库取出记录的值
			MyBeanUtils.copyBeanNotNull2Bean(goods, t);//将编辑表单中的非NULL值覆盖数据库记录中的值
			goodsService.save(t);//保存
		}else{//新增表单保存
			goodsService.save(goods);//保存
		}
		addMessage(redirectAttributes, "保存商品成功");
		return "redirect:"+Global.getAdminPath()+"/goods/goods/?repage";
	}
	
	/**
	 * 删除商品
	 */
	@RequiresPermissions("goods:goods:del")
	@RequestMapping(value = "delete")
	public String delete(Goods goods, RedirectAttributes redirectAttributes) {
		goodsService.delete(goods);
		addMessage(redirectAttributes, "删除商品成功");
		return "redirect:"+Global.getAdminPath()+"/goods/goods/?repage";
	}
	
	/**
	 * 批量删除商品
	 */
	@RequiresPermissions("goods:goods:del")
	@RequestMapping(value = "deleteAll")
	public String deleteAll(String ids, RedirectAttributes redirectAttributes) {
		String idArray[] =ids.split(",");
		for(String id : idArray){
			goodsService.delete(goodsService.get(id));
		}
		addMessage(redirectAttributes, "删除商品成功");
		return "redirect:"+Global.getAdminPath()+"/goods/goods/?repage";
	}
	
	/**
	 * 导出excel文件
	 */
	@RequiresPermissions("goods:goods:export")
    @RequestMapping(value = "export", method=RequestMethod.POST)
    public String exportFile(Goods goods, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "商品"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<Goods> page = goodsService.findPage(new Page<Goods>(request, response, -1), goods);
    		new ExportExcel("商品", Goods.class).setDataList(page.getList()).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导出商品记录失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/goods/goods/?repage";
    }

	/**
	 * 导入Excel数据

	 */
	@RequiresPermissions("goods:goods:import")
    @RequestMapping(value = "import", method=RequestMethod.POST)
    public String importFile(MultipartFile file, RedirectAttributes redirectAttributes) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<Goods> list = ei.getDataList(Goods.class);
			for (Goods goods : list){
				try{
					goodsService.save(goods);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条商品记录。");
			}
			addMessage(redirectAttributes, "已成功导入 "+successNum+" 条商品记录"+failureMsg);
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入商品失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/goods/goods/?repage";
    }
	
	/**
	 * 下载导入商品数据模板
	 */
	@RequiresPermissions("goods:goods:import")
    @RequestMapping(value = "import/template")
    public String importFileTemplate(HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "商品数据导入模板.xlsx";
    		List<Goods> list = Lists.newArrayList(); 
    		new ExportExcel("商品数据", Goods.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入模板下载失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/goods/goods/?repage";
    }
	
	
	/**
	 * 选择商品分类
	 */
	@RequestMapping(value = "selectcategory")
	public String selectcategory(GoodsCategory category, String url, String fieldLabels, String fieldKeys, String searchLabel, String searchKey, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<GoodsCategory> page = goodsService.findPageBycategory(new Page<GoodsCategory>(request, response),  category);
		try {
			fieldLabels = URLDecoder.decode(fieldLabels, "UTF-8");
			fieldKeys = URLDecoder.decode(fieldKeys, "UTF-8");
			searchLabel = URLDecoder.decode(searchLabel, "UTF-8");
			searchKey = URLDecoder.decode(searchKey, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		model.addAttribute("labelNames", fieldLabels.split("\\|"));
		model.addAttribute("labelValues", fieldKeys.split("\\|"));
		model.addAttribute("fieldLabels", fieldLabels);
		model.addAttribute("fieldKeys", fieldKeys);
		model.addAttribute("url", url);
		model.addAttribute("searchLabel", searchLabel);
		model.addAttribute("searchKey", searchKey);
		model.addAttribute("obj", category);
		model.addAttribute("page", page);
		return "modules/sys/gridselect";
	}
	

}