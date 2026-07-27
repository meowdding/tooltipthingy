package me.owdding.iconographic.config

import com.google.gson.JsonObject

val configPatches = buildList<(JsonObject) -> JsonObject> {
    add { json ->
        val misc = json.getAsJsonObject("misc") ?: JsonObject()
        val visuals = json.getAsJsonObject("visuals") ?: JsonObject()
        val pets = json.getAsJsonObject("pets") ?: JsonObject()
        val mining = json.getAsJsonObject("mining") ?: JsonObject()
        val newMisc = JsonObject()

        if (json.has("spinny")) visuals.add("spinny", json.remove("spinny"))
        if (json.has("vanillaBackground")) visuals.add("vanillaBackground", json.remove("vanillaBackground"))

        if (misc.has("skyBlockColor")) visuals.add("skyBlockColor", misc.remove("skyBlockColor"))
        if (misc.has("shinyHolographic")) visuals.add("shinyHolographic", misc.remove("shinyHolographic"))
        if (misc.has("alignedStats")) visuals.add("alignedStats", misc.remove("alignedStats"))

        if (misc.has("petAbilities")) pets.add("petAbilities", misc.remove("petAbilities"))
        if (misc.has("petHeldItem")) pets.add("petHeldItem", misc.remove("petHeldItem"))
        if (misc.has("petLevel")) pets.add("petLevel", misc.remove("petLevel"))
        if (misc.has("petFavourite")) pets.add("petFavourite", misc.remove("petFavourite"))

        if (misc.has("drillFuel")) mining.add("drillFuel", misc.remove("drillFuel"))
        if (misc.has("drillComponents")) mining.add("drillComponents", misc.remove("drillComponents"))

        if (misc.has("itemAbility")) newMisc.add("itemAbility", misc.remove("itemAbility"))
        if (misc.has("enchantedBookNames")) newMisc.add("enchantedBookNames", misc.remove("enchantedBookNames"))
        if (misc.has("skillLevelBar")) newMisc.add("skillLevelBar", misc.remove("skillLevelBar"))

        if (visuals.size() > 0) json.add("visuals", visuals)
        if (pets.size() > 0) json.add("pets", pets)
        if (mining.size() > 0) json.add("mining", mining)
        if (newMisc.size() > 0) json.add("misc", newMisc)

        json
    }
}
