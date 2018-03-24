package pl.sda;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HibernateTest {

    public static void main(String args[]){
        try(SessionFactory factory = new Configuration().configure("hibernate.cfg.xml").buildSessionFactory()){
            createSampleOrders(factory);
            getOrderById(factory, 1);
            getOrdersByDeliveryCity(factory, "Lodz");
            getOrdersByStatus(factory, OrderStatus.NEW);
            updateOrderStatus(factory, OrderStatus.DELIVERED, 1);
            getOrdersByStatus(factory, OrderStatus.NEW);
        }
    }

    private static void getOrderById(SessionFactory factory, Integer id) {
        try(Session session = factory.openSession()) {
            Order order = session.get(Order.class, id);
            System.out.println("ORDER BY ID = " + id + ":" + order);
        }
    }

    private static void getOrdersByDeliveryCity(SessionFactory factory, String deliveryCity) {
        try(Session session = factory.openSession()) {
            Query<Order> query = session.createQuery("from Order where deliveryAddress.city = :deliveryCityPlaceholder", Order.class);
            query.setParameter("deliveryCityPlaceholder", deliveryCity);
            List<Order> orders = query.list();
            System.out.println("getOrdersByDeliveryCity() - NUMBER OF RETRIEVED ORDERS: " + orders.size());
            orders.stream().forEach(System.out::println);
        }
    }

    private static void getOrdersByStatus(SessionFactory factory, OrderStatus status) {
        try(Session session = factory.openSession()) {
            Query<Order> query = session.createQuery("from Order where status = :statusPlaceholder", Order.class);
            query.setParameter("statusPlaceholder", status);
            List<Order> orders = query.list();
            System.out.println("getOrdersByStatus() - NUMBER OF RETRIEVED ORDERS: " + orders.size());
            orders.stream().forEach(System.out::println);
        }
    }

    private static void updateOrderStatus(SessionFactory factory, OrderStatus status, Integer orderId) {
        try (Session session = factory.openSession()) {
            Order order = session.get(Order.class, orderId);
            Transaction transaction = session.getTransaction();
            transaction.begin();
            order.setStatus(status);
            transaction.commit();
        }
    }

    private static void createSampleOrders(SessionFactory factory) {
        saveOrder(createSampleOrder(), factory);
        saveOrder(createSampleOrder2(), factory);
    }

    private static void saveOrder(Order order, SessionFactory factory) {
        System.out.println("ORDER BEFORE SAVE = " + order);
        try(Session session = factory.openSession()) {
            Transaction transaction = session.getTransaction();
            transaction.begin();
            Integer orderId = (Integer) session.save(order);
            System.out.println("Order CREATED with id = " + orderId);
            transaction.commit();
            System.out.println("ORDER AFTER SAVE = " + order);
        }
    }

    private static Order createSampleOrder() {
        Order order = new Order();
        order.setCustomerName("Piotr Zawadzki");
        order.setCustomerAddress(new Address("Lodz", "92-243", "Rewolucji 77"));
        order.setDeliveryAddress(new Address("Lodz", "92-243", "Żermoskiego 94c"));
        order.setStatus(OrderStatus.NEW);
        order.setCreationDate(new Date());

        List<OrderDetail> orderDetails = new ArrayList<>();
        order.setOrderDetails(orderDetails);

        OrderDetail orderDetail1 = new OrderDetail();
        orderDetail1.setProductName("REKAWICZKI NARCIARSKIE");
        orderDetail1.setProductPrice(BigDecimal.valueOf(150.0));
        orderDetail1.setQuantity(BigDecimal.ONE);
        orderDetail1.setOrder(order);
        orderDetails.add(orderDetail1);

        OrderDetail orderDetail2 = new OrderDetail();
        orderDetail2.setProductName("T-SHIRT");
        orderDetail2.setProductPrice(BigDecimal.valueOf(99.0));
        orderDetail2.setQuantity(new BigDecimal(2));
        orderDetail2.setOrder(order);
        orderDetails.add(orderDetail2);

        return order;
    }


    private static Order createSampleOrder2() {
        Order order = new Order();
        order.setCustomerName("Jan Kowalski");
        order.setCustomerAddress(new Address("Warszawa", "91-243", "Rewolucji 77"));
        order.setDeliveryAddress(new Address("Warszawa", "91-243", "Żermoskiego 94c"));
        order.setStatus(OrderStatus.NEW);
        order.setCreationDate(new Date());

        List<OrderDetail> orderDetails = new ArrayList<>();
        order.setOrderDetails(orderDetails);

        OrderDetail orderDetail1 = new OrderDetail();
        orderDetail1.setProductName("BLUZA");
        orderDetail1.setProductPrice(BigDecimal.valueOf(250.0));
        orderDetail1.setQuantity(BigDecimal.ONE);
        orderDetail1.setOrder(order);
        orderDetails.add(orderDetail1);

        OrderDetail orderDetail2 = new OrderDetail();
        orderDetail2.setProductName("SPODNIE");
        orderDetail2.setProductPrice(BigDecimal.valueOf(300.0));
        orderDetail2.setQuantity(new BigDecimal(1));
        orderDetail2.setOrder(order);
        orderDetails.add(orderDetail2);

        return order;
    }
}
