package com.github.vkvish19.batch.processing.configuration;

import com.github.vkvish19.batch.processing.listener.JobCompletionNotificationListener;
import com.github.vkvish19.batch.processing.objects.Person;
import com.github.vkvish19.batch.processing.objects.Student;
import com.github.vkvish19.batch.processing.processor.PersonItemProcessor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

@Configuration
public class BatchConfiguration
{
    @Bean
    public FlatFileItemReader<Student> reader()
    {
        return new FlatFileItemReaderBuilder<Student>()
                .name("personItemReader")
                .resource(new ClassPathResource("sample-data.csv"))
                .delimited()
                .names("firstName", "lastName", "age", "standard")
                .targetType(Student.class)
                .build();
    }

    @Bean
    public PersonItemProcessor processor()
    {
        return new PersonItemProcessor();
    }

    @Bean
    public JdbcBatchItemWriter<Person> writer(DataSource dataSource)
    {
        return new JdbcBatchItemWriterBuilder<Person>()
                .sql("INSERT INTO people (first_name, last_name) VALUES (:firstName, :lastName)")
                .dataSource(dataSource)
                .beanMapped()
                .build();
    }

    @Bean
    public Job importUserJob(JobRepository jobRepository, Step firstStep, JobCompletionNotificationListener listener)
    {
        return new JobBuilder("importUserJob", jobRepository)
                .listener(listener)
                .start(firstStep)
                .build();
    }

    @Bean
    public Step firstStep(JobRepository jobRepository, DataSourceTransactionManager transactionManager,
                          FlatFileItemReader<Student> reader, PersonItemProcessor processor, JdbcBatchItemWriter<Person> writer)
    {
        return new StepBuilder("firstStep", jobRepository)
                .<Student, Person>chunk(3, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }
}
