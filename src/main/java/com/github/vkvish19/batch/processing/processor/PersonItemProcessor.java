package com.github.vkvish19.batch.processing.processor;

import com.github.vkvish19.batch.processing.objects.Person;
import com.github.vkvish19.batch.processing.objects.Student;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

public class PersonItemProcessor implements ItemProcessor<Student, Person>
{
    private static final Logger log = LoggerFactory.getLogger(PersonItemProcessor.class);

    @Override
    public Person process(Student student) throws Exception
    {
        String firstName = student.firstName().toUpperCase();
        String lastName = student.lastName().toUpperCase();

        Person person = new Person(firstName, lastName);
        log.info("Converting ({}) into ({})", student, person);

        return person;
    }
}
