package com.nico.store.store.controller;

import com.nico.store.store.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.nico.store.store.service.OrderService;
import com.nico.store.store.service.ShoppingCartService;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.IOException;
import java.util.List;

@Controller
public class CheckoutControler {
	
	@Autowired 
	private ShoppingCartService shoppingCartService;
	
	@Autowired
	private OrderService orderService;

	@Autowired
	private JavaMailSender mailSender;

	@RequestMapping("/checkout")
	public String checkout( @RequestParam(value="missingRequiredField", required=false) boolean missingRequiredField,
							Model model, Authentication authentication) {		
		User user = (User) authentication.getPrincipal();	
		ShoppingCart shoppingCart = shoppingCartService.getShoppingCart(user);
		if(shoppingCart.isEmpty()) {
			model.addAttribute("emptyCart", true);
			return "redirect:/shopping-cart/cart";
		}						
		model.addAttribute("cartItemList", shoppingCart.getCartItems());
		model.addAttribute("shoppingCart", shoppingCart);
		if(missingRequiredField) {
			model.addAttribute("missingRequiredField", true);
		}		
		return "checkout";		
	}
	
//	@RequestMapping(value = "/checkout", method = RequestMethod.POST)
//	public String placeOrder(@ModelAttribute("shipping") Shipping shipping,
//							@ModelAttribute("address") Address address,
//							@ModelAttribute("payment") Payment payment,
//							 @ModelAttribute("cartItem") CartItem cartItem,
////			, 				@RequestParam(value = "file") MultipartFile file ,
//							RedirectAttributes redirectAttributes, Model model,Authentication authentication) throws MessagingException, IOException {
//		User user = (User) authentication.getPrincipal();
//
//		ShoppingCart shoppingCart = shoppingCartService.getShoppingCart(user);
//		if (!shoppingCart.isEmpty()) {
//			shipping.setAddress(address);
//			Order order = orderService.createOrder(shoppingCart, shipping, payment, user);
//
//			redirectAttributes.addFlashAttribute("order", order);
//
//			List<CartItem> cartItems = shoppingCart.getCartItems();
//			for(CartItem item: cartItems){
//				Article article = item.getArticle();
//				Order order1= item.getOrder();
//				Date date= order1.getOrderDate();
//				long IdOrder=order1.getId();
//				String tiltle= article.getTitle();
//				int quantity=item.getQty();
//				String size= item.getSize();
//				double price= article.getPrice();
//				String img=article.getPicture();
//
//
//				MimeMessage message = mailSender.createMimeMessage();
//				MimeMessageHelper helper = new MimeMessageHelper(message,true);
//
//				helper.setSubject("This is an HTML email");
//				helper.setTo(user.getEmail());
//				helper.setText("<html><head><style>body { background-color: #f4f4f4; } h1 { color: red; }</style></head><body>" +
//						"<b>THÔNG TIN ĐƠN HÀNG - DÀNH CHO NGƯỜI MUA</b>,<br>" +
//						"<br><i>Mã đơn hàng: #ORD</i>"+IdOrder+
//						"<br><i>Ngày đặt: </i>"+date+
//						"<br><img src='cid:image001' style='width: 150px; height:150px;'/><div>"+"\n"+
//						"<br><i>" +tiltle+"</i>"+"<br><i>Size: </i>"+size+
//						"<br><i>Số lượng: </i>"+quantity+"<br><i>Giá: </i>"+price+"</body></html>", true);
//				FileSystemResource resource = new FileSystemResource(new File("src/main/resources/static/image/article/pictures/"+img));
//				helper.addInline("image001", resource);
//				mailSender.send(message);
//
//				model.addAttribute("message", "An HTML email has been sent");
//			}
//		}
//		return "redirect:/order-submitted";
//	}

	@RequestMapping(value = "/checkout", method = RequestMethod.POST)
	public String placeOrder(@ModelAttribute("shipping") Shipping shipping,
						 	@ModelAttribute("address") Address address,
						 	@ModelAttribute("payment") Payment payment,
						 	RedirectAttributes redirectAttributes, Model model, Authentication authentication) throws MessagingException, IOException {
		User user = (User) authentication.getPrincipal();
		int index = 1;
		ShoppingCart shoppingCart = shoppingCartService.getShoppingCart(user);
		if (!shoppingCart.isEmpty()) {
			shipping.setAddress(address);
			Order order = orderService.createOrder(shoppingCart, shipping, payment, user);

			redirectAttributes.addFlashAttribute("order", order);

			StringBuilder html = new StringBuilder();
			html.append("<html><head><style>body { background-color: #f4f4f4; } h1 { color: red; }</style></head><body>" +
						"<b>Hello "+user.getUsername()+"</b>"+
						"<center><img src='cid:logo' alt='Logo' style='width: 250px; height:80px;'/><br>"+
						"<h3><b>THÔNG TIN ĐƠN HÀNG - DÀNH CHO NGƯỜI MUA</b></h3>," +
						"<br><b><i>Mã đơn hàng: #ORD</i>"+order.getId()+
						"</b><br><i>Ngày đặt: </i>"+order.getOrderDate());
			FileSystemResource logoResource = new FileSystemResource(new File("src/main/resources/static/image/logo2.png"));

			List<CartItem> cartItems = shoppingCart.getCartItems();
			for(CartItem item: cartItems){
				Article article = item.getArticle();
				String title = article.getTitle();
				int quantity = item.getQty();
				String size = item.getSize();
				double price = article.getPrice();
				String img = article.getPicture();
				String cid = "image" + index;
				html.append("<br>--------------------<br>"+"<br><img src='cid:" + cid + "' style='width: 150px; height:150px;'/><div>" + "\n" +
							"<br><b><i>" +index+". "+ title + "</i></b>" + "<br><i>Size: </i>" + size +
							"<br><i>Số lượng: </i>" + quantity + "<br><i>Giá: $" + price+"</i></center>");
				index++;
			}
			html.append("</body></html>");

			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message,true);

			helper.setSubject("This is an HTML email");
			helper.setTo(user.getEmail());
			helper.setText(html.toString(), true);

			index = 1;
			for(CartItem item: cartItems){
				Article article = item.getArticle();
				String img = article.getPicture();
				String cid = "image" + index;
				FileSystemResource resource = new FileSystemResource(new File("src/main/resources/static/image/article/pictures/" + img));
				helper.addInline(cid, resource);
				index++;
			}
			helper.addInline("logo", logoResource);
			mailSender.send(message);

			model.addAttribute("message", "An HTML email has been sent");
		}
		return "redirect:/order-submitted";
	}
	
	@RequestMapping(value = "/order-submitted", method = RequestMethod.GET)
	public String orderSubmitted(Model model) {
		Order order = (Order) model.asMap().get("order");
		if (order == null) {
			return "redirect:/";
		}
		model.addAttribute("order", order);
		return "orderSubmitted";	
	}

}
