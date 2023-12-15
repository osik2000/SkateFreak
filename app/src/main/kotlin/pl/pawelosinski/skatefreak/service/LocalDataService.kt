package pl.pawelosinski.skatefreak.service

import pl.pawelosinski.skatefreak.local.allTrickRecords
import pl.pawelosinski.skatefreak.local.allTrickInfo
import pl.pawelosinski.skatefreak.model.TrickInfo
import pl.pawelosinski.skatefreak.model.TrickRecord

class LocalDataService {
    companion object {
        fun getTrickRecord(id: String) : TrickRecord {
            return allTrickRecords.find { it.id == id } ?: TrickRecord()
        }

        fun getTrickInfo(id: String) : TrickInfo {
            return allTrickInfo.find { it.id == id } ?: TrickInfo()
        }
    }
}