package com.vaidegrails.simpleservices.crud

import grails.transaction.NotTransactional
import grails.transaction.Transactional
import grails.validation.ValidationErrors
import grails.validation.ValidationException

import java.lang.reflect.Method

@Transactional
class SimpleCrudService<GrailsDomainClass> implements AbstractCrudService<GrailsDomainClass> {

    private HashMap<MetaMethod, ArrayList<Closure>> methodsValidations

    SimpleCrudService() {
        super()
        methodsValidations = initializeValidations()
    }

    @Override
    GrailsDomainClass save(GrailsDomainClass domainInstance) throws ValidationException {
        ArrayList<ValidationException> methodValidationExceptions = performValidations(domainInstance, "save")
        if (!(methodValidationExceptions.empty)) {
            throw new ValidationException(
                    "Failed to save the ${domainInstance.class.simpleName} due to:", domainInstance.errors
            )
        }
        domainInstance.save(failOnError: true)
        return domainInstance
    }

    @Override
    GrailsDomainClass update(GrailsDomainClass domainInstance) throws ValidationException {
        return null
    }

    @Override
    ArrayList<GrailsDomainClass> list() {
        return null
    }

    @Override
    void delete(GrailsDomainClass domainInstance) throws ValidationException {

    }

    @Override
    @NotTransactional
    ArrayList<ValidationException> performValidations(GrailsDomainClass instance, final String methodName) {
        MetaMethod metaMethod = findMethod(methodName)
        ArrayList<Closure> methodValidations = findMethodValidations(metaMethod)
        ArrayList<ValidationErrors> exceptions = new ArrayList<ValidationErrors>()
        methodValidations.each { validations ->
            validations.each { validation ->
                def validationExceptions = validation(instance)
                if (validationExceptions != null) {
                    exceptions.addAll(validationExceptions)
                }
            }
        }
        return exceptions
    }

    @NotTransactional
    private ArrayList<Closure> findMethodValidations(MetaMethod metaMethod) {
        ArrayList<Closure> methodValidations = new ArrayList<Closure>()
        if (metaMethod != null && this.methodsValidations.containsKey(metaMethod)) {
            methodValidations.add(this.methodsValidations.get(metaMethod))
        }
        methodValidations
    }

    @NotTransactional
    MetaMethod findMethod(final String methodName) {
        this.metaClass.methods.find({ MetaMethod method -> method.name == methodName })
    }

    @Override
    @NotTransactional
    HashMap<Method, ArrayList<Closure>> initializeValidations() {
        def methodsValidations = new HashMap<Method, ArrayList<Closure>>()
        this.class.methods.each { method ->
            def methodValidationAnnotation = method.getAnnotation(MethodValidationAnnotation)
            if (methodValidationAnnotation) {
                def targetMethodName = methodValidationAnnotation?.appliesTo()
                MetaMethod targetMethod = findMethod(targetMethodName)
                methodsValidations.put(targetMethod, [this.&"${method.name}"])
            }
        }
        return methodsValidations
    }

    /**
     * Perform the default validation to the method save
     * @param instance Instance that must be validated
     * @return All the exceptions found
     */
    @MethodValidationAnnotation(appliesTo = "save")
    def saveDefaultValidation(GrailsDomainClass instance) {
        ValidationErrors exceptions = null
        if (!instance.validate()) {
            exceptions = new ValidationErrors(instance)
            exceptions.addAllErrors(instance.errors)
        }
        return exceptions
    }
}
