package br.com.mbausp.eda.product.auto.route;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.camel.Exchange;
import org.apache.camel.ValidationException;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import br.com.mbausp.eda.product.auto.config.PropertiesConfig;
import br.com.mbausp.eda.product.auto.domain.AutoCliente;
import br.com.mbausp.eda.product.auto.domain.AutoClientePayload;
import br.com.mbausp.eda.product.auto.domain.AutoStatus;
import br.com.mbausp.eda.product.auto.entity.AutoClienteEntity;
import br.com.mbausp.eda.product.auto.exception.NotFoundException;
import br.com.mbausp.eda.product.auto.exception.UnavailableException;
import br.com.mbausp.eda.product.auto.repository.AutoClienteRepository;

@Component
public class AutoRoute extends RouteBuilder {

	private static final int HTTP_ERROR_CODE_400 = 400;

	private static final int HTTP_ERROR_CODE_404 = 404;

	private static final int HTTP_ERROR_CODE_503 = 503;

	private final AutoClienteRepository autoClienteRepository;

	private final PropertiesConfig props;

    public AutoRoute(AutoClienteRepository autoUsuarioRepository, PropertiesConfig props) {
    	this.autoClienteRepository = autoUsuarioRepository;
    	this.props = props;
	}

	@Override
    public void configure() throws Exception {

		onException(NotFoundException.class)
    	.id("on_exception_id")
    		.handled(true)
    		.setBody(ex -> {
    			return Map.of("message", "auto não encontrado");
    		})
    		.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(HTTP_ERROR_CODE_404))
    	.end();

		onException(UnavailableException.class)
    	.id("on_exception_un_id")
    		.handled(true)
    		.setBody(ex -> {
    			return Map.of("message", "serviço indisponivel");
    		})
    		.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(HTTP_ERROR_CODE_503))
    	.end();

    	onException(ValidationException.class)
	        .handled(true)
        	.setBody(ex -> {
    			return Map.of("message", "verifique os campos obrigatórios");
	        })
        	.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(HTTP_ERROR_CODE_400))
        .end();

        from(RouteEnum.DIRECT_CONSULTAR_AUTO.getRoute())
        .routeId(RouteEnum.DIRECT_CONSULTAR_AUTO.getRouteId())
	        .choice()
	        	.when(exchange -> AutoRoute.this.props.isUnavailable())
	        	.throwException(new UnavailableException())
	        .end()
	        .setBody(ex -> {
	        	var id = ex.getIn().getHeader("id", Long.class);
	        	var entity = this.autoClienteRepository.findById(id).orElseThrow(() -> new NotFoundException());
	        	return new AutoCliente(entity.getId(),
	        			entity.getNuContrato(),
	        			entity.getSeqContrato(),
	        			entity.getClienteId(),
	        			entity.getDataCriacao(),
	        			entity.getAutoAtivo(),
	        			entity.getDataAlteracao(),
	        			entity.getStatus());
	        })
	        .process(ex -> {
	        	// simular latencia
	        	var min = (int) AutoRoute.this.props.getMinLatencyInMilli();
	        	var max = (int) AutoRoute.this.props.getMaxLatencyInMilli();
	        	var randomNum = ThreadLocalRandom.current().nextInt(min, max);
	        	Thread.sleep(randomNum);
	        })
        .end();

        from(RouteEnum.DIRECT_CRIAR_AUTO.getRoute())
        .routeId(RouteEnum.DIRECT_CRIAR_AUTO.getRouteId())
        	.to("bean-validator://autoValidator")
	        .setBody(ex -> {
	        	var input = ex.getIn().getBody(AutoClientePayload.class);
	        	var sEntity = new AutoClienteEntity();
	        	sEntity.setClienteId(input.idCliente());
	        	sEntity.setNuContrato(input.contractNumber());
	        	sEntity.setSeqContrato(input.seqContract());
	        	sEntity.setAutoAtivo(true);
	        	sEntity.setStatus(AutoStatus.ATIVO);
	        	sEntity.setDataCriacao(LocalDateTime.now());
	        	var rEntity = this.autoClienteRepository.save(sEntity);
	        	return new AutoCliente(rEntity.getId(),
	        			rEntity.getNuContrato(),
	        			rEntity.getSeqContrato(),
	        			rEntity.getClienteId(),
	        			rEntity.getDataCriacao(),
	        			rEntity.getAutoAtivo(),
	        			rEntity.getDataAlteracao(),
	        			rEntity.getStatus());
	        })
	        .setProperty("result", body())
	        .to(RouteEnum.KAFKA_NOTIFICAR_AUTO.getRoute())
	        	.id("to_kafka_notificar_conta_id")
	        .setBody(exchangeProperty("result"))
        .end();

    }

}
