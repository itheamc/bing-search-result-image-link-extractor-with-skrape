import it.skrape.fetcher.HttpFetcher
import it.skrape.fetcher.Method
import it.skrape.fetcher.response
import it.skrape.fetcher.skrape
import kotlinx.coroutines.delay
import org.json.JSONObject

suspend fun main() {
    val skills = fetchSkills()

    for (skill in skills) {
        if (skill.id > 3713) {
            try {
                val images = ImageExtractor.findImageLinks(skill.name.lowercase())
                val filteredImages = images.filter { it.length <= 120}
                storeImages(images = filteredImages, id = skill.id)
                delay(200)
            } catch (e: Exception) {
                continue
            }
        }
    }
}


data class Skill(val id: Int, val name: String)


suspend fun fetchSkills(): ArrayList<Skill> {
    val response = skrape(HttpFetcher) {
        request {
            url = "http://127.0.0.1:8000/jobs/skills"
            method = Method.GET
        }
        response {
            """{"skills": ${this.responseBody}}"""
        }
    }

    val skills: ArrayList<Skill> = arrayListOf()
    val jObject = JSONObject(response)
    val jArray = jObject.getJSONArray("skills")
    jArray.forEach { j ->
        skills.add(
            Skill(
                id = (j as JSONObject).getInt("id"),
                name = j.getString("title"),
            )
        )
    }

    return skills
}


suspend fun storeImages(images: List<String>, id: Int) {
    skrape(HttpFetcher) {
        request {
            url = "http://127.0.0.1:8000/jobs/add_images"
            method = Method.POST
            body {

                json {
                    "job_skill_id" to id
                    "images" to images
                }
            }
        }
        response {
            println(this.responseBody)
        }
    }
}
