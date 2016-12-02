package com.vaidegrails.simpleservices

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import grails.validation.ValidationException
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(SimpleClassExampleService)
@Mock([SimpleClassExample])
class SimpleClassExampleServiceSpec extends Specification {

    def setup() {
    }

    def cleanup() {
    }

    void "Save a instance with no data (invalid) throws ValidationException"() {
        given: "A new instance without data"
        SimpleClassExample simpleClassExampleInstance = new SimpleClassExample()

        when: "Saves the instance"
        service.save(simpleClassExampleInstance)


        then: "A validation exception is thrown"
        ValidationException e = thrown(ValidationException)

        and: "The message is the following"
        e.message.startsWith("Failed to save the SimpleClassExample due to:")
    }

    void "Save a instance with all required data (valid) persists the instance"() {
        given: "A new instance with valid data"
        SimpleClassExample teste = new SimpleClassExample(aString: "Test", aNumber: 1, aDate: new Date())

        when: "Saves the instance"
        SimpleClassExample savedTeste = service.save(teste)

        then: "The returned instance is not null"
        savedTeste != null

        and: "Has the same data then the original instance"
        savedTeste.aString == teste.aString
        savedTeste.aNumber == teste.aNumber
    }
}
