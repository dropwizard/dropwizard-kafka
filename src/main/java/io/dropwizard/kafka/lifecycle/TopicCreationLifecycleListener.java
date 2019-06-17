package io.dropwizard.kafka.lifecycle;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.eclipse.jetty.util.component.LifeCycle;

import java.util.Collection;

public abstract class TopicCreationLifecycleListener implements LifeCycle.Listener {
    private final AdminClient adminClient;
    private final Collection<NewTopic> topics;

    public TopicCreationLifecycleListener(AdminClient client, Collection<NewTopic> topics) {
        this.adminClient = client;
        this.topics = topics;
    }

    @Override
    public void lifeCycleStarted(LifeCycle lifeCycle) {
        // TODO: Check if topics already exist
        this.adminClient.createTopics(topics);
    }
}
