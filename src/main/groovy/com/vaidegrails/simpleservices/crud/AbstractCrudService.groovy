package com.vaidegrails.simpleservices.crud

import grails.validation.ValidationException

interface AbstractCrudSerice<GrailsDomainClass> {

    /**
     * Saves an instance from the domain class in the datasource specified by the project
     * @param domainInstance The instance to be saved
     * @return The saved instance
     * @throws ValidationException If the instance can't be saved because of a constraint
     */
    GrailsDomainClass save(GrailsDomainClass domainInstance) throws ValidationException

    /**
     * Updates an instance from the domain class in the datasource specified by the project
     * @param domainInstance The instance to be updated
     * @return The updated instance
     * @throws ValidationException If the instance can't be updated because of a constraint
     */
    GrailsDomainClass update(GrailsDomainClass domainInstance) throws ValidationException

    /**
     * List all instances from the domain class that are in the datasource
     * @return List of all instances
     */
    ArrayList<GrailsDomainClass> list()

    /**
     * Deletes an instance from the database
     * @param domainInstance The instance to be deleted
     * @throws ValidationException
     */
    void delete(GrailsDomainClass domainInstance) throws ValidationException

    /**
     * Perform all the validations associated with the given method in an instance
     * @param instance The instance that will be validated
     * @param method The method to perform validations
     * @return The list of validations associated with the method
     */
    ArrayList<ValidationException> performValidations(GrailsDomainClass instance, final String method)

    /**
     * Retrieves, if it exists, the metamethod of the class that has the name passed as parameter.
     * @param methodName Name of the method
     * @return The metamethod
     */
    MetaMethod findMethod(final String methodName)
}