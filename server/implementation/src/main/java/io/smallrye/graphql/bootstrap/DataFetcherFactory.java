package io.smallrye.graphql.bootstrap;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import org.dataloader.BatchLoaderWithContext;

import graphql.schema.DataFetcher;
import io.smallrye.graphql.execution.datafetcher.CompletableFutureDataFetcher;
import io.smallrye.graphql.execution.datafetcher.DefaultDataFetcher;
import io.smallrye.graphql.execution.datafetcher.UniDataFetcher;
import io.smallrye.graphql.schema.model.Field;
import io.smallrye.graphql.schema.model.Operation;
import io.smallrye.graphql.schema.model.Wrapper;
import io.smallrye.mutiny.Uni;

/**
 * Create the datafetchers for a certain operation
 *
 * @author Phillip Kruger (phillip.kruger@redhat.com)
 */
public class DataFetcherFactory {

    private final Config config;

    public DataFetcherFactory(Config config) {
        this.config = config;
    }

    public <T> DataFetcher<T> getDataFetcher(Operation operation) {
        return (DataFetcher<T>) get(operation);
    }

    public <K, T> BatchLoaderWithContext<K, T> getSourceBatchLoader(Operation operation) {
        return (BatchLoaderWithContext<K, T>) get(operation);
    }

    public Wrapper unwrap(Field field, boolean isBatch) {
        if (isAsync(field) && isBatch) {
            return field.getWrapper().getWrapper().getWrapper();
        } else if (isAsync(field)) {
            return field.getWrapper().getWrapper();
        } else if (isBatch) {
            return field.getWrapper().getWrapper();
        } else if (field.hasWrapper() && field.getWrapper().isCollectionOrArray()) {
            return field.getWrapper();
        } else if (field.hasWrapper()) {
            // TODO: Move Generics logic here ?
        }
        return null;
    }

    // TODO: Have some way to load custom ?    
    private <V> V get(Operation operation) {
        if (isJDKFuture(operation)) {
            return (V) new CompletableFutureDataFetcher(operation, config);
        } else if (isMutinyUni(operation)) {
            return (V) new UniDataFetcher(operation, config);
        }
        return (V) new DefaultDataFetcher(operation, config);
    }

    private boolean isAsync(Field field) {
        return isJDKFuture(field) || isMutinyUni(field);
    }

    private boolean isJDKFuture(Field field) {
        if (field.hasWrapper()) {
            String wrapperClassName = field.getWrapper().getWrapperClassName();
            return wrapperClassName.equals(CompletableFuture.class.getName())
                    || wrapperClassName.equals(CompletionStage.class.getName());
        }
        return false;
    }

    private boolean isMutinyUni(Field field) {
        if (field.hasWrapper()) {
            String wrapperClassName = field.getWrapper().getWrapperClassName();
            return wrapperClassName.equals(Uni.class.getName());
        }
        return false;
    }
}
