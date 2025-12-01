package com.startup.trucking.util;

import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class PaymentIdGeneratorTest {

    @Test
    void test_getInstance_returns_same_singleton() {
        PaymentIdGenerator a = PaymentIdGenerator.getInstance();
        PaymentIdGenerator b = PaymentIdGenerator.getInstance();
        assertSame(a, b, "getInstance() should return the same singleton instance");
    }

    @Test
    void test_getInstance_threadSafe_singleInstanceAcrossThreads() throws Exception {
        int threads = 50;
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        try {
            Callable<PaymentIdGenerator> task = PaymentIdGenerator::getInstance;

            var futures = IntStream.range(0, threads)
                    .mapToObj(i -> pool.submit(task))
                    .toList();

            PaymentIdGenerator first = futures.get(0).get(3, TimeUnit.SECONDS);
            for (Future<PaymentIdGenerator> f : futures) {
                assertSame(first, f.get(3, TimeUnit.SECONDS),
                        "All threads should observe the same singleton instance");
            }
        } finally {
            pool.shutdownNow();
        }
    }

    @Test
    void test_nextId_hasPrefix_and_valid_uuid() {
        PaymentIdGenerator gen = PaymentIdGenerator.getInstance();
        String id = gen.nextId();

        assertNotNull(id);
        assertTrue(id.startsWith("PAY-"), "ID must start with 'PAY-'");

        String uuidPart = id.substring(4);
        // validate UUID format by parsing
        assertDoesNotThrow(() -> UUID.fromString(uuidPart), "Suffix must be a valid UUID");
        // simple regex check for length/structure (36 chars with hyphens)
        assertTrue(uuidPart.matches("[0-9a-fA-F\\-]{36}"), "UUID part should be 36 chars with hyphens");
    }

    @Test
    void test_nextId_uniqueness_over_many_calls() {
        PaymentIdGenerator gen = PaymentIdGenerator.getInstance();
        int n = 500;
        Set<String> ids = IntStream.range(0, n)
                .mapToObj(i -> gen.nextId())
                .collect(Collectors.toSet());

        assertEquals(n, ids.size(), "IDs should be unique across multiple generations");
        assertTrue(ids.stream().allMatch(s -> s.startsWith("PAY-")), "All IDs should start with 'PAY-'");
    }

    @Test
    void test_nextId_differs_between_consecutive_calls() {
        PaymentIdGenerator gen = PaymentIdGenerator.getInstance();
        String a = gen.nextId();
        String b = gen.nextId();
        assertNotEquals(a, b, "Consecutive IDs should not be equal");
    }
}
