package org.k3yake


import com.ninja_squad.dbsetup_kotlin.DbSetupBuilder
import com.ninja_squad.dbsetup_kotlin.dbSetup
import org.assertj.db.api.Assertions
import org.assertj.db.type.Changes
import org.assertj.db.type.Table
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import javax.sql.DataSource


/**
 * Created by katsuki-miyake on 18/03/06.
 */
@RunWith(SpringRunner::class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
class CityApiTestBySpringBootTest {

    @Autowired
    lateinit var mockServer: MockMvc
    @Autowired
    lateinit var dataSource: DataSource

    @Test
    fun postの仕様_未登録の国の都市の場合_都市と国を登録する() {
        //準備
        dbSetup(to = dataSource) {
            deleteAll()
        }.launch()

        //実行
        mockServer.perform(MockMvcRequestBuilders.post("/city")
                .content("""{"name":"ebisu", "country":"Japan"}""".toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().json("""{id=1}""".toString()))

        //確認
        Assertions.assertThat(Table(dataSource, "country"))
                .hasNumberOfRows(1)
                .row(0)
                .value("name").isEqualTo("Japan")
        Assertions.assertThat(Table(dataSource, "city"))
                .hasNumberOfRows(1)
                .row(0)
                .value("name").isEqualTo("ebisu")
    }

    @Test
    fun postの仕様_登録済みの国の都市の場合_都市のみを登録する() {
        //準備
        dbSetup(to = dataSource) {
            deleteAll()
            insertInto("country") {
                columns("id", "name")
                values(1, "Japan")
            }
        }.launch()

        //実行
        mockServer.perform(MockMvcRequestBuilders.post("/city")
                .content("""{"name":"ebisu", "country":"Japan"}""".toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().json("""{id=1}""".toString()))

        //確認
        Assertions.assertThat(Table(dataSource, "country"))
                .hasNumberOfRows(1)
                .row(0)
                .value("name").isEqualTo("Japan")
        Assertions.assertThat(Table(dataSource, "city"))
                .hasNumberOfRows(1)
                .row(0)
                .value("name").isEqualTo("ebisu")
    }

    @Test
    fun postの仕様_登録済みの都市の場合_エラーとなり登録は行われない() {
        dbSetup(to = dataSource) {
            deleteAll()
            insertInto("country") {
                columns("id", "name")
                values(1, "Japan")
            }
            insertInto("city") {
                columns("id", "name", "country_id")
                values(1, "ebisu", 1)
            }
        }.launch()
        val changees = Changes(dataSource).setStartPointNow()

        //実行
        mockServer.perform(MockMvcRequestBuilders.post("/city")
                .content("""{"name":"ebisu", "country":"Japan"}""".toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().`is`(HttpStatus.CONFLICT.value()))

        //確認
        changees.setEndPointNow()
        Assertions.assertThat(changees).hasNumberOfChanges(0)
    }
}

fun DbSetupBuilder.deleteAll() {
    val tables = listOf("city", "country")
    deleteAllFrom(tables)
    tables.forEach {
        sql("ALTER TABLE ${it} ALTER COLUMN id RESTART WITH 1")
    }
}

