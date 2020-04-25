package domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

internal class BodyParserTest {
    @Test
    @DisplayName("json 형식의 body가 잘 파싱되는지")
    internal fun parse() {
        val body = "{\n" +
                "    \"token\": \"Jhj5dZrVaK7ZwHHjRyZWjbDl\",\n" +
                "    \"challenge\": \"3eZbrw1aBm2rZgRNFdxV2595E9CY3gmdALWMmHkvFXO7tYXAYM8P\",\n" +
                "    \"type\": \"url_verification\"\n" +
                "}"
        val bodyParser = BodyParser(body)

        assertThat(bodyParser.getValueOf("token")).isEqualTo("Jhj5dZrVaK7ZwHHjRyZWjbDl")
    }

    @Test
    @DisplayName("존재하지 않는 key")
    internal fun parseNullKey() {
        val body = "{\n" +
                "    \"token\": \"Jhj5dZrVaK7ZwHHjRyZWjbDl\",\n" +
                "    \"challenge\": \"3eZbrw1aBm2rZgRNFdxV2595E9CY3gmdALWMmHkvFXO7tYXAYM8P\",\n" +
                "    \"type\": \"url_verification\"\n" +
                "}"
        val bodyParser = BodyParser(body)
        assertThat(bodyParser.getValueOf("token", "null")).isNull()
    }

    @Test
    @DisplayName("존재하지 않는 key")
    internal fun parseNullKey2() {
        val body = "{\n" +
                "    \"token\": \"Jhj5dZrVaK7ZwHHjRyZWjbDl\",\n" +
                "    \"challenge\": \"3eZbrw1aBm2rZgRNFdxV2595E9CY3gmdALWMmHkvFXO7tYXAYM8P\",\n" +
                "    \"type\": \"url_verification\"\n" +
                "}"
        val bodyParser = BodyParser(body)
        assertThat(bodyParser.getValueOf("null")).isNull()
    }
}