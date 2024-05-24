package com.driver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

@Repository
public class OrderRepository {

	private HashMap<String, Order> orderMap;
	private HashMap<String, DeliveryPartner> partnerMap;
	private HashMap<String, HashSet<String>> partnerToOrderMap;
	private HashMap<String, String> orderToPartnerMap;
	

	public OrderRepository() {
		this.orderMap = new HashMap<String, Order>();
		this.partnerMap = new HashMap<String, DeliveryPartner>();
		this.partnerToOrderMap = new HashMap<String, HashSet<String>>();
		this.orderToPartnerMap = new HashMap<String, String>();
	}

	public void saveOrder(Order order) {
		// your code here
		if(order == null) {
			throw new NullPointerException("Order is Empty");
		}
		orderMap.put(order.getId(), order);
	}

	public void savePartner(String partnerId) {
		if(partnerId == null) {
			throw new NullPointerException("PartnerId is not provided");
		}
		DeliveryPartner dp = new DeliveryPartner(partnerId);
		partnerMap.put(partnerId, dp);
		// your code here
		// create a new partner with given partnerId and save it
	}

	public void saveOrderPartnerMap(String orderId, String partnerId) {
		if(orderId == null || partnerId== null) {
			throw new NullPointerException("OrderId or PartnerId cannot be null");
		
		}
		if (orderMap.containsKey(orderId) && partnerMap.containsKey(partnerId)) {
			// your code here
			// add order to given partner's order list
			HashSet<String> orderPartnerSet ;
			if(!partnerToOrderMap.containsKey(partnerId)) {
				orderPartnerSet=new HashSet<>();
				orderPartnerSet.add(orderId);
			}else {
				orderPartnerSet = partnerToOrderMap.get(partnerId);//--check partnerId available in partnerOrderMap
				//--check oredrId is already present or not
				if(orderToPartnerMap.containsKey(orderId))
					throw new IllegalArgumentException(orderId+" is already assigned to another delivery partner");
				else
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
		if(orderId == null) {
			throw new NullPointerException("OrderId cannot be null");
		
		}
		Order order = orderMap.get(orderId);
		return order;
	}

	public DeliveryPartner findPartnerById(String partnerId) {
//		return null;
		// your code here
		if(partnerId== null) {
			throw new NullPointerException("PartnerId cannot be null");
		
		}
		
		DeliveryPartner deliveryPartner= partnerMap.get(partnerId);
		return deliveryPartner;
	}

	public Integer findOrderCountByPartnerId(String partnerId) {
		if(partnerId== null) {
			throw new NullPointerException("PartnerId cannot be null");
		
		}
//		return null;
		// your code here
		HashSet<String> partnerOrderSet = partnerToOrderMap.get(partnerId);
		if(partnerOrderSet==null)
			return 0;
		
		return partnerOrderSet.size();
		
	}

	public List<String> findOrdersByPartnerId(String partnerId) {
		if(partnerId== null) {
			throw new NullPointerException("PartnerId cannot be null");
		
		}
		//return null;
		// your code here
		HashSet<String> partnerOrderSet = partnerToOrderMap.get(partnerId);
		if(partnerOrderSet==null)
			return new ArrayList<>();
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
		if(partnerId== null) {
			throw new NullPointerException("PartnerId cannot be null");
		
		}
		// your code here
		// delete partner by ID
		//delete partner from partnerMap
		DeliveryPartner dp =partnerMap.remove(partnerId);
		//make the order unassigned
		//--to make order assign , make changes to orderToPartnerMap
		/*for(String key:orderToPartnerMap.keySet()) {
			if(orderToPartnerMap.get(key).equals(partnerId))
				orderToPartnerMap.remove(key);//This will cause ConcurrentModificationException
		}*/
		Set<String> orderKeys = orderToPartnerMap.keySet();
		Iterator<String> iterator = orderKeys.iterator();
		while (iterator.hasNext()) {
		    String key = iterator.next();
		    if (orderToPartnerMap.get(key).equals(partnerId)) {
		        iterator.remove(); // This safely removes the element during iteration
		    }
	    }
		//---remove orderlist from partnerToOrderMap
		partnerToOrderMap.remove(partnerId);
		
	}

	public void deleteOrder(String orderId) {
		if(orderId== null) {
			throw new NullPointerException("orderId cannot be null");
		
		}
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
		if(partnerId== null || timeString == null) {
			throw new NullPointerException("PartnerId or timeString cannot be null");
		
		}
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
		if(partnerId== null) {
			throw new NullPointerException("PartnerId cannot be null");
		
		}
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