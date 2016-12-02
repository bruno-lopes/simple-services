package com.vaidegrails.simpleservices.crud

import grails.transaction.NotTransactional
import grails.transaction.Transactional
import grails.validation.ValidationErrors
import grails.validation.ValidationException

import java.lang.reflect.Method

@Transactional
class SimpleCrudService<GrailsDomainClass> implements AbstractCrudSerice<GrailsDomainClass> {

    private HashMap<MetaMethod, ArrayList<Closure>> methodsValidations

    SimpleCrudService() {
        super()
        methodsValidations = new HashMap<Method, ArrayList<Closure>>()
        def saveMethod = findMethod("save")
        methodsValidations.put(saveMethod, saveValidation)
    }

    def saveValidation = { GrailsDomainClass instance ->
//        ArrayList<ValidationException> exceptions = new ArrayList<ValidationException>()
        ValidationErrors exceptions = null
        println "Instancia:${instance.properties}"
        if (!instance.validate()) {
            println "Instancia nao valida"
            exceptions = new ValidationErrors(instance)
            //exceptions.add(new ValidationExcenption("aff", instance.errors))
            exceptions.addAllErrors(instance.errors)

        }
        return exceptions
    }

    @Override
    GrailsDomainClass save(GrailsDomainClass domainInstance) throws ValidationException {

        ArrayList<ValidationException> methodValidationExceptions = performValidations(domainInstance, "save")
        if (!(domainInstance.validate()) || !(methodValidationExceptions.empty)) {
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
        println "Validationgs: ${instance.validate()}"
        methodValidations.each { validation ->

            def validationExceptions = validation(instance)
            if (validationExceptions != null) {
                exceptions.addAll(validationExceptions)
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
}
