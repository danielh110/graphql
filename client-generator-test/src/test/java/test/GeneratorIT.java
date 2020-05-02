package test;

import static org.assertj.core.api.Assertions.contentOf;
import static org.assertj.core.api.BDDAssertions.then;

import java.io.File;

import org.junit.jupiter.api.Test;

class GeneratorIT {
    @Test
    void shouldHaveGeneratedApi() {
        then(contentOf(file("SuperHeroesApi"))).isEqualTo("" +
                "package io.smallrye.graphql.client.generator.test;\n" +
                "\n" +
                "import java.util.List;\n" +
                "import org.eclipse.microprofile.graphql.Query;\n" +
                "\n" +
                "public interface SuperHeroesApi {\n" +
                "    List<Team> teams();\n" +
                "    @Query(\"heroesIn\") List<SuperHero> heroesLocatedIn(String location);\n" +
                "}\n");
    }

    @Test
    void shouldHaveGeneratedTeam() {
        then(contentOf(file("Team"))).isEqualTo("" +
                "package io.smallrye.graphql.client.generator.test;\n" +
                "\n" +
                "public class Team {\n" +
                "    String name;\n" +
                "}\n");
    }

    @Test
    void shouldHaveGeneratedSuperHero() {
        then(contentOf(file("SuperHero"))).isEqualTo("" +
                "package io.smallrye.graphql.client.generator.test;\n" +
                "\n" +
                "import java.util.List;\n" +
                "\n" +
                "public class SuperHero {\n" +
                "    String name;\n" +
                "    List<String> superPowers;\n" +
                "    String realName;\n" +
                "}\n");
    }

    private File file(String file) {
        return new File("target/generated-sources/annotations/io/smallrye/graphql/client/generator/test/" + file + ".java");
    }
}
