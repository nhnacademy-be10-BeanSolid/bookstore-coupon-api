package com.nhnacademy.config;

import com.nhnacademy.common.config.QuerydslConfig;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;

class QuerydslConfigTest {

    @Test
    void jpaQueryFactoryBeanCreation() {
        QuerydslConfig config = new QuerydslConfig();

        EntityManager em = Mockito.mock(EntityManager.class);

        try {
            java.lang.reflect.Field field = QuerydslConfig.class.getDeclaredField("entityManager");
            field.setAccessible(true);
            field.set(config, em);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        JPAQueryFactory factory = config.jpaQueryFactory();

        assertThat(factory).isNotNull();
    }
}

