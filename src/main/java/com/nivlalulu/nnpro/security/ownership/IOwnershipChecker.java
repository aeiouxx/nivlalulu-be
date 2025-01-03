package com.nivlalulu.nnpro.security.ownership;

import com.nivlalulu.nnpro.model.User;

// todo: We only need to check ownership for the user entity, so we assume the ownerId to be Long.
public interface IOwnershipChecker<T> {

    /**
     * The entity class of the resource.
     * @return
     */
    Class<?> getEntityClass();
    /**
     * Checks whether the resource with the given ID exists.
     * @param resourceId
     * @return
     */
    boolean resourceExists(T resourceId);

    /**
     * Checks whether the resource with the given ID is owned by the given user.
     */
    boolean isOwnedBy(T resourceId, User user);
}
