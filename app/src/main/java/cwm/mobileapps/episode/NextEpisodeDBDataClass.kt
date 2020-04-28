package cwm.mobileapps.episode

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