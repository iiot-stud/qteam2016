package org.qTeam.api.core;

public interface MessageFilters {

    String FILTER_ORCHESTRATOR = IMessageBus.METHOD_TARGET + " = '" + IMessageBus.TARGET_ORCHESTRATOR + "' "
            + "AND (" + IMessageBus.METHOD_TYPE + " = '" + IMessageBus.TYPE_CONFIGURE + "' "
            + "OR " + IMessageBus.METHOD_TYPE + " = '" + IMessageBus.TYPE_DELETE + "' "
            + "OR " + IMessageBus.METHOD_TYPE + " = '" + IMessageBus.TYPE_CREATE + "' "
            + "OR " + IMessageBus.METHOD_TYPE + " = '" + IMessageBus.TYPE_GET + "' ) "
            + "OR " + IMessageBus.METHOD_TYPE + " = '" + IMessageBus.TYPE_INFORM + "'";

    String FILTER_RESERVATION = IMessageBus.METHOD_TARGET + " = '" + IMessageBus.TARGET_RESERVATION + "' "
            + "AND (" + IMessageBus.METHOD_TYPE + " = '" + IMessageBus.TYPE_CREATE + "' "
            + "OR " + IMessageBus.METHOD_TYPE + " = '" + IMessageBus.TYPE_CONFIGURE + "' "
            + "OR " + IMessageBus.METHOD_TYPE + " = '" + IMessageBus.TYPE_GET + "')";

    String FILTER_RESOURCE_ADAPTER_MANAGER = IMessageBus.METHOD_TARGET + " = '" + IMessageBus.TARGET_RESOURCE_ADAPTER_MANAGER + "'"
            + "AND (" + IMessageBus.METHOD_TYPE + " = '" + IMessageBus.TYPE_CREATE + "' "
            + "OR " + IMessageBus.METHOD_TYPE + " = '" + IMessageBus.TYPE_GET + "' "
            + "OR " + IMessageBus.METHOD_TYPE + " = '" + IMessageBus.TYPE_DELETE + "')";

    String FILTER_FEDERATION_MANAGER = IMessageBus.METHOD_TARGET + " = '" + IMessageBus.TARGET_FEDERATION_MANAGER + "'"
            + "AND (" + IMessageBus.METHOD_TYPE + " = '" + IMessageBus.TYPE_GET + "' "
            + "OR " + IMessageBus.METHOD_TYPE + " = '" + IMessageBus.TYPE_CONFIGURE + "' "
            + "OR " + IMessageBus.METHOD_TYPE + " = '" + IMessageBus.TYPE_DELETE + "')";

    String FILTER_ADAPTER = IMessageBus.METHOD_TARGET + " = '" + IMessageBus.TARGET_ADAPTER + "'"
            + "AND (" + IMessageBus.METHOD_TYPE + " = '" + IMessageBus.TYPE_CREATE + "' "
            + "OR " + IMessageBus.METHOD_TYPE + " = '" + IMessageBus.TYPE_CONFIGURE + "' "
            + "OR " + IMessageBus.METHOD_TYPE + " = '" + IMessageBus.TYPE_GET + "' "
            + "OR " + IMessageBus.METHOD_TYPE + " = '" + IMessageBus.TYPE_DELETE + "')";

}
