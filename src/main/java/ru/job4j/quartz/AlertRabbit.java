package ru.job4j.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Properties;

import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;

public class AlertRabbit {

    private Connection initConnection(Properties properties) throws IOException, ClassNotFoundException, SQLException {
        try (InputStream in = AlertRabbit.class.getClassLoader().getResourceAsStream("rabbit.properties")) {
            properties.load(in);
        }
        Class.forName(properties.getProperty("connection.driver_class"));
        String url = properties.getProperty("connection.url");
        String login = properties.getProperty("connection.username");
        String password = properties.getProperty("connection.password");
        return DriverManager.getConnection(url, login, password);
    }

    public static void main(String[] args) {
        try {
            Properties properties = new Properties();
            AlertRabbit alertRabbit = new AlertRabbit();
            Connection conn = alertRabbit.initConnection(properties);
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            JobDataMap data = new JobDataMap();
            data.put("conn", conn);
            JobDetail job = newJob(Rabbit.class)
                    .usingJobData(data)
                    .build();
            SimpleScheduleBuilder times = simpleSchedule()
                    .withIntervalInSeconds(2)
                    .repeatForever();
            Trigger trigger = newTrigger()
                    .startNow()
                    .withSchedule(times)
                    .build();
            scheduler.scheduleJob(job, trigger);
            Thread.sleep(10000);
            scheduler.shutdown();
        } catch (Exception se) {
            se.printStackTrace();
        }
    }

    public static class Rabbit implements Job {

        public Rabbit() {
            System.out.println(hashCode());
        }

        @Override
        public void execute(JobExecutionContext context) {
            System.out.println("Rabbit runs here ...");
            Connection conn = (Connection) context.getJobDetail().getJobDataMap().get("conn");
            try (PreparedStatement statement = conn.prepareStatement(
                    "insert into rabbit(created_date) values (?);")) {
                statement.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now().withNano(0)));
                statement.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}