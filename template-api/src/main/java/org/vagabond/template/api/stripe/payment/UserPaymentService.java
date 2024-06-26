package org.vagabond.template.api.stripe.payment;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.stripe.model.PaymentIntent;

import org.vagabond.common.notification.NotificationService;
import org.vagabond.common.notification.payload.NotificationRequest;
import org.vagabond.common.user.UserEntity;
import org.vagabond.common.user.UserService;
import org.vagabond.engine.crud.repository.BaseRepository;
import org.vagabond.engine.crud.service.BaseService;
import org.vagabond.engine.exeption.MetierException;

@ApplicationScoped
public class UserPaymentService extends BaseService<UserPaymentEntity> {

    private static final int PLAN_MONTH = 30;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Inject
    private UserPaymentRepository creatorPaymentRepository;

    @Inject
    private UserService userService;

    @Inject
    private NotificationService notificationService;

    @Override
    public BaseRepository<UserPaymentEntity> getRepository() {
        return creatorPaymentRepository;
    }

    public UserPaymentEntity createPayment(UserEntity user, String intentId, String intentSecret, PaymentIntent intent) {
        var nbPayment = countBy("where paymentId = ?1", intent.getId());
        if (nbPayment > 0) {
            throw new MetierException("ERRORS:PAYMENT_FOUND");
        }
        var creatorPayment = new UserPaymentEntity();
        creatorPayment.intent = intentId;
        creatorPayment.secret = intentSecret;
        creatorPayment.amount = intent.getAmount();
        creatorPayment.amountReceived = intent.getAmountReceived();
        creatorPayment.currency = intent.getCurrency();
        creatorPayment.paymentId = intent.getId();
        creatorPayment.paymentMethod = intent.getPaymentMethod();
        creatorPayment.source = intent.getSource();
        creatorPayment.status = intent.getStatus();
        persist(creatorPayment);

        // FIXME : put endPlan into USER - add role plan for user with a dateEnd
        // var endPlan = LocalDateTime.now();
        // if (user.endPlan != null && LocalDateTime.now().isBefore(user.endPlan)) {
        // endPlan = user.endPlan;
        // }
        // user.endPlan = endPlan.plus(PLAN_MONTH, ChronoUnit.DAYS);
        userService.persist(user);

        var title = "Abonnement actif";
        var description = "Votre abonnement est actif jusqu'au : "; // + user.endPlan.format(formatter);
        var notification = new NotificationRequest(title, description, "/creator/profile");
        notificationService.sendNotification(user, Arrays.asList(user.id), notification, user.id, "CREATOR", "SUBSCRIPTION", "CREATE");

        return creatorPayment;
    }
}