package com.driver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

@Repository
public class OrderRepository {

	private HashMap<String, Order> orderMap;
	private HashMap<String, DeliveryPartner> partnerMap;
	private HashMap<String, HashSet<String>> partnerToOrderMap;
	private HashMap<String, String> orderToPartnerMap;
	
	private List<String> unassigneOrderList;

	public OrderRepository() {
		this.orderMap = new HashMap<String, Order>();
		this.partnerMap = new HashMap<String, DeliveryPartner>();
		this.partnerToOrderMap = new HashMap<String, HashSet<String>>();
		this.orderToPartnerMap = new HashMap<String, String>();
	}

	public void saveOrder(Order order) {
		// your code here
		orderMap.put(order.getId(), order);
	}

	public void savePartner(String partnerId) {
		DeliveryPartner dp = new DeliveryPartner(partnerId);
		partnerMap.put(partnerId, dp);
		// your code here
		// create a new partner with given partnerId and save it
	}

	public void saveOrderPartnerMap(String orderId, String partnerId) {
		if (orderMap.containsKey(orderId) && partnerMap.containsKey(partnerId)) {
			// your code here
			// add order to given partner's order list
			HashSet<String> orderPartnerSet ;
			if(!partnerToOrderMap.containsKey(partnerId)) {
				orderPartnerSet=new HashSet<>();
				orderPartnerSet.add(orderId);
			}else {
				orderPartnerSet = partnerToOrderMap.get(partnerId);//--check partnerId available in partnerOrderMap
				orderPartnerSet.add(orderId);
			}
			partnerToOrderMap.put(partnerId, orderPartnerSet);
			// increase order count of partner
			DeliveryPartner dp = partnerMap.get(partnerId);
			dp.setNumberOfOrders(dp.getNumberOfOrders() + 1);
			partnerMap.put(partnerId, dp);
			// assign partner to this order
			orderToPartnerMap.put(orderId, partnerId);
		}
	}

	public Order findOrderById(String orderId) {
//		return null;
		// your code here
		Order order = orderMap.get(orderId);
		return order;
	}

	public DeliveryPartner findPartnerById(String partnerId) {
//		return null;
		// your code here
		DeliveryPartner deliveryPartner= partnerMap.get(partnerId);
		return deliveryPartner;
	}

	public Integer findOrderCountByPartnerId(String partnerId) {
		
//		return null;
		// your code here
		HashSet<String> partnerOrderSet = partnerToOrderMap.get(partnerId);
		return partnerOrderSet.size();
		
	}

	public List<String> findOrdersByPartnerId(String partnerId) {
		//return null;
		// your code here
		HashSet<String> partnerOrderSet = partnerToOrderMap.get(partnerId);
		if(partnerOrderSet.isEmpty())
			return null;
		List<String> partnerOrderList =new ArrayList<>(partnerOrderSet); 
		return partnerOrderList ;
	}

	public List<String> findAllOrders() {
		Set<String> orderSet = orderMap.keySet();
		if(orderSet.isEmpty())
			return null;
		List<String> orderList = new ArrayList<>(orderSet);
		return orderList;
		// your code here
		// return list of all orders
	}

	public void deletePartner(String partnerId) {
		// your code here
		// delete partner by ID
		//delete partner from partnerMap
		DeliveryPartner dp =partnerMap.remove(partnerId);
		//make the order unassigned
		//--to make order assign , make changes to orderToPartnerMap
		for(String key:orderToPartnerMap.keySet()) {
			if(orderToPartnerMap.get(key).equals(key))
				orderToPartnerMap.remove(key);
		}
		//---remove orderlist from partnerToOrderMap
		partnerToOrderMap.remove(partnerId);
		
	}

	public void deleteOrder(String orderId) {
		// your code here
		// delete order by ID
		Order order=orderMap.remove(orderId);
		//remove order from orderpartenerMap
		orderToPartnerMap.remove(orderId);
		//remove orderId from set of partenerOrder where set of order is present
		for(String key:partnerToOrderMap.keySet()) {
			HashSet<String> set=partnerToOrderMap.get(key);
			if(set.contains(orderId)) {
				set.remove(orderId);
				partnerToOrderMap.put(key, set);
				break;
			}
		}
	}

	public Integer findCountOfUnassignedOrders() {
		Integer count=0;
		for(String orderKey:orderMap.keySet()) {
			if(!orderToPartnerMap.containsKey(orderKey))
				count++;
		}
		return count ;
		// your code here
	}

	public Integer findOrdersLeftAfterGivenTimeByPartnerId(String timeString, String partnerId) {
		String[] time=timeString.split(":");
		Integer count=0;
		int givenTime=Integer.parseInt(time[0])*60+Integer.parseInt(time[1]);
		HashSet<String> orderSet = partnerToOrderMap.get(partnerId);
		for(String orderKey:orderSet) {
			Order order=orderMap.get(orderKey);
			if(givenTime<order.getDeliveryTime())
				count++;
		}
		return count;
		// your code here
	}

	public String findLastDeliveryTimeByPartnerId(String partnerId) {
		
		// your code here
		// code should return string in format HH:MM
		Integer time=Integer.MIN_VALUE;
		HashSet<String> orderSet = partnerToOrderMap.get(partnerId);
		for(String orderKey:orderSet) {
			Order order=orderMap.get(orderKey);
			if(time<order.getDeliveryTime()) {
				time=order.getDeliveryTime();
			}
		}
		int hrQuetiont=time/60;
		int minRemainder=time%60;
		String timeString="";
//		if(hrQuetiont/10==0) 
//			timeString+="0"+hrQuetiont;
//		else
//			timeString+=hrQuetiont;
//		
//		if(minRemainder/10==0) 
//			timeString+=":0"+minRemainder;
//		else
//			timeString+=":"+minRemainder;
		
		if(hrQuetiont/10==0) 
			timeString+="0";
		
		timeString+=hrQuetiont+":";
		
		if(minRemainder/10==0) 
			timeString+="0";
		
		timeString+=minRemainder;
		
		return timeString;
			
	}
}