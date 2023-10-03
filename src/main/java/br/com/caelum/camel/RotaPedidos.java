package br.com.caelum.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.http4.HttpMethods;
import org.apache.camel.impl.DefaultCamelContext;

public class RotaPedidos {

	public static void main(String[] args) throws Exception {

		CamelContext context = new DefaultCamelContext();
		context.addRoutes(new RouteBuilder() {
			
			@Override
			public void configure() throws Exception {
				// TODO Auto-generated method stub
				
				from("file:pedidos?delay=5s&noop=true").
					setProperty("pedidoId", xpath("/pedido/id/text()")).//Esta dando o nome da propriedade e indicando onde ela esta 
				    setProperty("clienteId", xpath("/pedido/pagamento/email-titular/text()")).//Esta dando o nome da propriedade e indicando onde ela esta 
				split().xpath("pedido/itens/item").
				log("${body}").
				filter().xpath("item/formato[text()='EBOOK']").
					setProperty("ebookId", xpath("/item/livro/codigo/text()")).//Esta dando o nome da propriedade e indicando onde ela esta 
				marshal().xmljson().
				log("${body}").
					setHeader(Exchange.HTTP_METHOD, HttpMethods.GET).
					setHeader(Exchange.HTTP_QUERY, simple("clienteId=${property.clienteId}&pedidoId=${property.pedidoId}&ebookId=${property.ebookId}")).//Esta  informando que vai ser uma consulta ge e esta passando os parametros da consulta
				to("http4://localhost:8080/webservices/ebook/item");
				
			}
		});
		
		System.out.println("Começou!");
		context.start();
		Thread.sleep(2000);
		context.stop();
		System.out.println("Finalizou");
	}	
}
