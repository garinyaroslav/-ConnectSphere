package org.garin.core.client;

import org.garin.core.entity.SubscriptionType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class SubscriptionClient {

  @Value("${app.client.subscription-service.change-subscription}")
  private String changeSubscriptionUrl;

  @Value("${app.client.subscription-service.delete-subscription-by-id}")
  private String deleteSubscriptionByIdUrl;

  @Value("${app.client.subscription-service.username}")
  private String username;

  @Value("${app.client.subscription-service.password}")
  private String password;

  private final RestClient restClient;

  public void subscribe(Long id, Long subscriberId) {
    sendChangeSubscriptionRequest(id, subscriberId, SubscriptionType.SUBSCRIBE);
  }

  public void unsubscribe(Long id, Long subscriberId) {
    sendChangeSubscriptionRequest(id, subscriberId, SubscriptionType.UNSUBSCRIBE);
  }

  public void deleteSubscription(Long subscriptionId) {
    log.info("Send subscription request by id {}", subscriptionId);

    var response = restClient.delete()
        .uri(deleteSubscriptionByIdUrl, subscriptionId)
        .headers(headers -> headers.setBasicAuth(username, password))
        .retrieve()
        .toEntity(Void.class);

    log.info("Delete subscription response status: {}", response.getStatusCode());
  }

  private void sendChangeSubscriptionRequest(Long id, Long subscriberId, SubscriptionType subscriptionType) {
    log.info("Send change subscription request for id {} and subscriberId {} and type {}", id, subscriberId,
        subscriptionType);

    var response = restClient.post().uri(changeSubscriptionUrl)
        .headers(headers -> headers.setBasicAuth(username, password))
        .body(new ChangeSubscriptionRequest(id, subscriberId, subscriptionType))
        .retrieve().toEntity(ChangeSubscriptionResponse.class);

    log.info("Change subscription response status: {}", response.getStatusCode());
    log.info("Change subscription response body: {}", response.getBody());
  }

}
