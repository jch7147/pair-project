package com.example.demo;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AccountController {

	@Autowired
	HttpSession session;

	@Autowired
	UserRepository userRepository;

	/**
	 * ログイン.サインアップ画面を表示
	 */
	@RequestMapping("/")
	public String login() {
		// セッション情報はクリアする
		//session.invalidate();
		return "login";
	}

	/**
	 * ログインを実行
	 */
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public ModelAndView doLogin(
			@RequestParam("email") String email,
			@RequestParam("password") String password,
			ModelAndView mv) {

		List<User_info> user_info = userRepository.findByEmailAndPassword(email, password);
		//		if (customers.size() > 0) {
		//			// メールアドレスとパスワードがDBにあるやつと一致したらログインOK
		//			// リストの1件目をログインユーザとして取得する
		//			Customer customer = customers.get(0);
		//			session.setAttribute("customerInfo", customer);
		//
		//			// セッションスコープにカテゴリ情報を格納する
		//			session.setAttribute("categories", categoryRepository.findAll());
		//			// top.htmlを表示する
		//			mv.setViewName("top");

		return mv;
	}
}
