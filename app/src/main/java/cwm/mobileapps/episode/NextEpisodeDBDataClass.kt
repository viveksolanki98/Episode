package cwm.mobileapps.episode
//This is the data class for the data that comes from the SQL database
class NextEpisodeDBDataClass {
    var showID : String =""
    var episodeID : String =""

    constructor(showID:String, episodeID:String){
        this.showID = showID
        this.episodeID = episodeID
    }

    constructor(){
        this.showID = showID
        this.episodeID = episodeID
    }
}