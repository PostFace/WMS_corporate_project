package com.purchase_sheet.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spring.domain.ClientVO;
import com.spring.domain.MemberVO;
import com.spring.domain.OrderSheetDetailVO;
import com.spring.domain.OrderSheetVO;
import com.spring.domain.PageDTO;
import com.spring.domain.Purchase_sheetVO;
import com.spring.paging.Criteria;
import com.spring.service.ClientService;
import com.spring.service.MemberService;
import com.spring.service.Purchase_sheetService;


//@Controller
@RestController
@RequestMapping("/purchase_sheet/*")
public class Purchase_sheetRestController {
	
	@Autowired
	private Purchase_sheetService service;
	
	@Autowired
	private ClientService cs;
	
	@Autowired 
	private MemberService ms;
	
	//testURL : http://localhost:8080/basicinfo/member/pages/1/10
	@GetMapping(
			value={"/pages/{pageNum}/{amount}", "/pages/{pageNum}/{amount}/{whatColumn}", "/pages/{pageNum}/{amount}/{whatColumn}/{keyword}"}, 
			produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<PageDTO<Purchase_sheetVO>> Purchase_sheetlist(
			@PathVariable("pageNum") int pageNum,
			@PathVariable("amount") int amount,
			@PathVariable(value="whatColumn", required = false) String whatColumn,
			@PathVariable(value="keyword", required = false) String keyword) {				
		

		System.out.println(pageNum + " " + amount + " " + whatColumn + " " + keyword);
		
		Criteria cri = new Criteria(pageNum, amount, whatColumn, keyword);
		
		return new ResponseEntity<>(service.getListPage(cri), HttpStatus.OK);		
	}
	
	@GetMapping(value={"/selectOrder/{no}"}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Map<String, Object>> selectOrder(@PathVariable("no") int no) {
		System.out.println(1);
		//수주번호 한가지 조회
		Purchase_sheetVO vo = service.selectOneByMainNo(no);
		
		System.out.println(2);
		vo.setNo(no);
		
		//수주 디테일 조회
		System.out.println(3);
		List<OrderSheetDetailVO> list = service.getSubList(no);
		
		System.out.println(4);
		for(OrderSheetDetailVO osdv : list) {
			osdv.setNo(osdv.getNo());
			osdv.setItem_no(osdv.getItem_no());
			osdv.setItem_code(osdv.getItem_code());
			osdv.setItem_name(osdv.getItem_name());
			osdv.setAmount(osdv.getAmount());
			
			//물품 거래처 조회
			ClientVO osdvCv = cs.selectOne(Integer.toString(osdv.getClient_no()));
			osdv.setClient_name(osdvCv.getName());
			
		}
		
		System.out.println(5);
		for(OrderSheetDetailVO osdv : list) {
			
			System.out.println("osdv.getItem_no():" + osdv.getItem_no());
			System.out.println("osdv.getItem_code()" + osdv.getItem_code());
			System.out.println("osdv.getItem_name()" + osdv.getItem_name());
			
			System.out.println("osdv.getName()" + osdv.getClient_name());
		}
		
		System.out.println(6);
		
		//거래처조회
		ClientVO cv = cs.selectOne(Integer.toString(vo.getClient_no()));
		
		vo.setClient_no(cv.getNo());
		vo.setClient_code(cv.getCode());
		vo.setClient_name(cv.getName());
		System.out.println(7);
		
		//맴버조회
		MemberVO mv = ms.getMemberByNo(vo.getMember_no());
		System.out.println("vo.getMember_no()" + vo.getMember_no());
		
		vo.setMember_no(mv.getNo());
		vo.setDep_name(mv.getDep_name());
		vo.setMember_name(mv.getName());
		
		System.out.println("mv.getDep_name()"+ mv.getDep_name());
		
		// 두가지 객체 보내기
		
		Map<String, Object> map = new HashMap<>();
		
		map.put("detailList", list);
		map.put("order", vo);
		
		
		return new ResponseEntity<>(map, HttpStatus.OK);		
	}
}
