package com.fleecast.stamina.notetaking;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.Spanned;

import com.fleecast.stamina.R;
import com.fleecast.stamina.chathead.MyApplication;
import com.fleecast.stamina.models.AudioNoteInfoRealmStruct;
import com.fleecast.stamina.models.AudioNoteInfoStruct;
import com.fleecast.stamina.models.RealmAudioNoteHelper;
import com.fleecast.stamina.utility.Constants;
import com.fleecast.stamina.utility.ExternalStorageManager;

import java.io.File;
import java.io.FileFilter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public final class Shakespeare{

    private final String folderToPlay;
    private final String mFileDbUniqueToken;
    private final  RealmAudioNoteHelper realmAudioNoteHelper;
    private final Context mContext;
    private MyApplication myApplication;

    //public List <AudioNoteInfoStruct> stackPlaylist = new ArrayList<>();

    public Shakespeare(Context mContext, String mFileDbUniqueToken) {

        this.mContext = mContext;
        this.mFileDbUniqueToken = mFileDbUniqueToken;
        this.folderToPlay = getPathToAudioFiles();

        realmAudioNoteHelper = new RealmAudioNoteHelper(mContext);
        myApplication = (MyApplication) mContext.getApplicationContext();


    }


    /**
     * Our data, part 1.
     */
    public Spanned[] loadAudioListForListViewAdapter() {

        File folder = new File(folderToPlay);

        File[] listOfFiles = folder.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                //String name = pathname.getName().toLowerCase();
                //return name.endsWith(".xml") && pathname.isFile();
                return pathname.isFile() && !pathname.isHidden();
            }
        });

        if (listOfFiles == null)
            return new Spanned[] {Html.fromHtml(Constants.CONST_STRING_NO_DESCRIPTION)};


        Arrays.sort(listOfFiles, new Comparator() {
            public int compare(Object o1, Object o2) {

                if (((File) o1).lastModified() < ((File) o2).lastModified()) {
                    return -1;
                } else if (((File) o1).lastModified() > ((File) o2).lastModified()) {
                    return +1;
                } else {
                    return 0;
                }
            }


        });


        Spanned[] htmlArrayForList = new Spanned[listOfFiles.length];

        List<AudioNoteInfoRealmStruct> audioNoteInfoStruct = new ArrayList<>(realmAudioNoteHelper.findAllAudioNotesByParentId(Integer.valueOf(mFileDbUniqueToken)));
        String htmlCompose;

        for (int i = 0; i < listOfFiles.length; i++) {

            boolean hasHitInDatabase = false;
            int dbId = getDbIdFromFileName(listOfFiles[i].getName());

            if (audioNoteInfoStruct.size() > 0) {

            int indexInDbStruct = lookInsideListForDbKey(audioNoteInfoStruct, dbId);
            if (indexInDbStruct>Constants.CONST_NULL_MINUS) {
                    htmlCompose = "<font color='" + getHexStringFromInt(R.color.air_force_blue) + "'>" + unixTimeToReadable((long) dbId) + "</font><br>" +
                            "<font color='" + getHexStringFromInt(R.color.gray_asparagus) + "'>" + audioNoteInfoStruct.get(indexInDbStruct).getTitle() + "</font><br><br>" +
                            "<font color='" + getHexStringFromInt(R.color.air_force_blue) + "'>" + audioNoteInfoStruct.get(indexInDbStruct).getDescription() + "</font>";
                    htmlArrayForList[i] = Html.fromHtml(htmlCompose);
                    hasHitInDatabase = true;
                }

            }

            if (!hasHitInDatabase) {
                htmlCompose = "<font color='" + getHexStringFromInt(R.color.gray_asparagus) + "'>"+Constants.CONST_STRING_NO_NOTE+ "</font><br>"+
                        "<small><font color='" + getHexStringFromInt(R.color.air_force_blue) + "'>" + unixTimeToReadable((long) dbId) + "</font></small>" ;

                htmlArrayForList[i] = Html.fromHtml(htmlCompose);
            }


        }

        return htmlArrayForList;
    }


    public void loadAudioListForPlayerService() {

        //First we try to kill the current working player service.
      /*  Intent intent = new Intent(mContext,PlayerService.class);
        intent.setAction(Constants.ACTION_STOP);
        mContext.startService(intent);
*/
        //Empty global playlist
        myApplication.stackPlaylist.clear();
        myApplication.setIndexSomethingIsPlaying(Constants.CONST_NULL_MINUS);

        File folder = new File(folderToPlay);

        File[] listOfFiles = folder.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                //String name = pathname.getName().toLowerCase();
                //return name.endsWith(".xml") && pathname.isFile();
                return pathname.isFile() && !pathname.isHidden();
            }
        });


        Arrays.sort(listOfFiles, new Comparator() {
            public int compare(Object o1, Object o2) {

                if (((File) o1).lastModified() < ((File) o2).lastModified()) {
                    return -1;
                } else if (((File) o1).lastModified() > ((File) o2).lastModified()) {
                    return +1;
                } else {
                    return 0;
                }
            }


        });


        List<AudioNoteInfoRealmStruct> audioNoteInfoStruct = new ArrayList<>(realmAudioNoteHelper.findAllAudioNotesByParentId(Integer.valueOf(mFileDbUniqueToken)));

        for (int i = 0; i < listOfFiles.length; i++) {

            boolean hasHitInDatabase = false;
            int dbId = getDbIdFromFileName(listOfFiles[i].getName());

            if (audioNoteInfoStruct.size() > 0) {

                int indexInDbStruct = lookInsideListForDbKey(audioNoteInfoStruct, dbId);
                if (indexInDbStruct>Constants.CONST_NULL_MINUS) {
                    myApplication.stackPlaylist.add(i,new AudioNoteInfoStruct(dbId, audioNoteInfoStruct.get(indexInDbStruct).getParentDbId(),folderToPlay +File.separator + listOfFiles[i].getName(),audioNoteInfoStruct.get(indexInDbStruct).getTitle(),audioNoteInfoStruct.get(indexInDbStruct).getDescription(),audioNoteInfoStruct.get(indexInDbStruct).getTag() ));
                    hasHitInDatabase = true;
                }

            }

            if (!hasHitInDatabase) {
                myApplication.stackPlaylist.add(i,new AudioNoteInfoStruct(dbId,Integer.valueOf(mFileDbUniqueToken),folderToPlay +File.separator + listOfFiles[i].getName(),null,null,Constants.CONST_NULL_ZERO));
            }


        }
    }



    private String getHexStringFromInt(int resourceColorId){
        //ContextCompat.getColor(mContext, R.color.color_name)
        int intColor = ContextCompat.getColor(mContext, resourceColorId);
        return "#" + String.valueOf(Integer.toHexString(intColor)).substring(2);
    }

private String unixTimeToReadable(long unixSeconds){

    Date date = new Date(unixSeconds*1000L); // *1000 is to convert seconds to milliseconds
    // E, dd MMM yyyy HH:mm:ss z
    SimpleDateFormat sdf = new SimpleDateFormat("E, dd/MM/yyyy HH:mm:ss a"); // the format of your date
    //sdf.setTimeZone(TimeZone.getTimeZone("GMT-4")); // give a timezone reference for formating (see comment at the bottom
    String formattedDate = sdf.format(date);
    //System.out.println(formattedDate);
    return formattedDate;

}
private int lookInsideListForDbKey(List<AudioNoteInfoRealmStruct> adNFo, int filePostFixId){

    int foundedDbIdKeyIndex=Constants.CONST_NULL_MINUS;
    for(int i=0 ; i < adNFo.size() ; i++){

        if(adNFo.get(i).getId()==filePostFixId){
            foundedDbIdKeyIndex=i;
            break;
        }


    }
    return foundedDbIdKeyIndex ;

}


    private int getDbIdFromFileName(String file_name)
    {

        if(file_name==null || file_name.length()==0)
            return -1;
        else
           return Integer.valueOf(file_name.substring(file_name.lastIndexOf("_") + 1));
    }

    private String getPathToAudioFiles() {

        String pathToRecordingDirectory = ExternalStorageManager.getWorkingDirectory() + File.separator +  mFileDbUniqueToken;

        return pathToRecordingDirectory;

    }


    private class ImageGetter implements Html.ImageGetter {
        @Override
        public Drawable getDrawable(String source) {
            int id;


                id = R.drawable.ic_action_armchair;

            Drawable d = mContext.getResources().getDrawable(id);
            d.setBounds(0,0,d.getIntrinsicWidth(),d.getIntrinsicHeight());
            return d;
        }
    };

    /**
     * Our data, part 2.
     */
    public static final String[] DIALOGUE = 
    {
            "So shaken as we are, so wan with care," +
            "Find we a time for frighted peace to pant," +
            "And breathe short-winded accents of new broils" +
            "To be commenced in strands afar remote." +
            "No more the thirsty entrance of this soil" +
            "Shall daub her lips with her own children's blood;" +
            "Nor more shall trenching war channel her fields," +
            "Nor bruise her flowerets with the armed hoofs" +
            "Of hostile paces: those opposed eyes," +
            "Which, like the meteors of a troubled heaven," +
            "All of one nature, of one substance bred," +
            "Did lately meet in the intestine shock" +
            "And furious close of civil butchery" +
            "Shall now, in mutual well-beseeming ranks," +
            "March all one way and be no more opposed" +
            "Against acquaintance, kindred and allies:" +
            "The edge of war, like an ill-sheathed knife," +
            "No more shall cut his master. Therefore, friends," +
            "As far as to the sepulchre of Christ," +
            "Whose soldier now, under whose blessed cross" +
            "We are impressed and engaged to fight," +
            "Forthwith a power of English shall we levy;" +
            "Whose arms were moulded in their mothers' womb" +
            "To chase these pagans in those holy fields" +
            "Over whose acres walk'd those blessed feet" +
            "Which fourteen hundred years ago were nail'd" +
            "For our advantage on the bitter cross." +
            "But this our purpose now is twelve month old," +
            "And bootless 'tis to tell you we will go:" +
            "Therefore we meet not now. Then let me hear" +
            "Of you, my gentle cousin Westmoreland," +
            "What yesternight our council did decree" +
            "In forwarding this dear expedience.",
            
            "Hear him but reason in divinity," + 
            "And all-admiring with an inward wish" + 
            "You would desire the king were made a prelate:" + 
            "Hear him debate of commonwealth affairs," + 
            "You would say it hath been all in all his study:" + 
            "List his discourse of war, and you shall hear" + 
            "A fearful battle render'd you in music:" + 
            "Turn him to any cause of policy," + 
            "The Gordian knot of it he will unloose," + 
            "Familiar as his garter: that, when he speaks," + 
            "The air, a charter'd libertine, is still," + 
            "And the mute wonder lurketh in men's ears," + 
            "To steal his sweet and honey'd sentences;" + 
            "So that the art and practic part of life" + 
            "Must be the mistress to this theoric:" + 
            "Which is a wonder how his grace should glean it," + 
            "Since his addiction was to courses vain," + 
            "His companies unletter'd, rude and shallow," + 
            "His hours fill'd up with riots, banquets, sports," + 
            "And never noted in him any study," + 
            "Any retirement, any sequestration" + 
            "From open haunts and popularity.",

            "I come no more to make you laugh: things now," +
            "That bear a weighty and a serious brow," +
            "Sad, high, and working, full of state and woe," +
            "Such noble scenes as draw the eye to flow," +
            "We now present. Those that can pity, here" +
            "May, if they think it well, let fall a tear;" +
            "The subject will deserve it. Such as give" +
            "Their money out of hope they may believe," +
            "May here find truth too. Those that come to see" +
            "Only a show or two, and so agree" +
            "The play may pass, if they be still and willing," +
            "I'll undertake may see away their shilling" +
            "Richly in two short hours. Only they" +
            "That come to hear a merry bawdy play," +
            "A noise of targets, or to see a fellow" +
            "In a long motley coat guarded with yellow," +
            "Will be deceived; for, gentle hearers, know," +
            "To rank our chosen truth with such a show" +
            "As fool and fight is, beside forfeiting" +
            "Our own brains, and the opinion that we bring," +
            "To make that only true we now intend," +
            "Will leave us never an understanding friend." +
            "Therefore, for goodness' sake, and as you are known" +
            "The first and happiest hearers of the town," +
            "Be sad, as we would make ye: think ye see" +
            "The very persons of our noble story" +
            "As they were living; think you see them great," +
            "And follow'd with the general throng and sweat" +
            "Of thousand friends; then in a moment, see" +
            "How soon this mightiness meets misery:" +
            "And, if you can be merry then, I'll say" +
            "A man may weep upon his wedding-day.",
            
            "First, heaven be the record to my speech!" + 
            "In the devotion of a subject's love," + 
            "Tendering the precious safety of my prince," + 
            "And free from other misbegotten hate," + 
            "Come I appellant to this princely presence." + 
            "Now, Thomas Mowbray, do I turn to thee," + 
            "And mark my greeting well; for what I speak" + 
            "My body shall make good upon this earth," + 
            "Or my divine soul answer it in heaven." + 
            "Thou art a traitor and a miscreant," + 
            "Too good to be so and too bad to live," + 
            "Since the more fair and crystal is the sky," + 
            "The uglier seem the clouds that in it fly." + 
            "Once more, the more to aggravate the note," + 
            "With a foul traitor's name stuff I thy throat;" + 
            "And wish, so please my sovereign, ere I move," + 
            "What my tongue speaks my right drawn sword may prove.",
            
            "Now is the winter of our discontent" + 
            "Made glorious summer by this sun of York;" + 
            "And all the clouds that lour'd upon our house" + 
            "In the deep bosom of the ocean buried." + 
            "Now are our brows bound with victorious wreaths;" + 
            "Our bruised arms hung up for monuments;" + 
            "Our stern alarums changed to merry meetings," + 
            "Our dreadful marches to delightful measures." + 
            "Grim-visaged war hath smooth'd his wrinkled front;" + 
            "And now, instead of mounting barded steeds" + 
            "To fright the souls of fearful adversaries," + 
            "He capers nimbly in a lady's chamber" + 
            "To the lascivious pleasing of a lute." + 
            "But I, that am not shaped for sportive tricks," + 
            "Nor made to court an amorous looking-glass;" + 
            "I, that am rudely stamp'd, and want love's majesty" + 
            "To strut before a wanton ambling nymph;" + 
            "I, that am curtail'd of this fair proportion," + 
            "Cheated of feature by dissembling nature," + 
            "Deformed, unfinish'd, sent before my time" + 
            "Into this breathing world, scarce half made up," + 
            "And that so lamely and unfashionable" + 
            "That dogs bark at me as I halt by them;" + 
            "Why, I, in this weak piping time of peace," + 
            "Have no delight to pass away the time," + 
            "Unless to spy my shadow in the sun" + 
            "And descant on mine own deformity:" + 
            "And therefore, since I cannot prove a lover," + 
            "To entertain these fair well-spoken days," + 
            "I am determined to prove a villain" + 
            "And hate the idle pleasures of these days." + 
            "Plots have I laid, inductions dangerous," + 
            "By drunken prophecies, libels and dreams," + 
            "To set my brother Clarence and the king" + 
            "In deadly hate the one against the other:" + 
            "And if King Edward be as true and just" + 
            "As I am subtle, false and treacherous," + 
            "This day should Clarence closely be mew'd up," + 
            "About a prophecy, which says that 'G'" + 
            "Of Edward's heirs the murderer shall be." + 
            "Dive, thoughts, down to my soul: here" + 
            "Clarence comes.",
            
            "To bait fish withal: if it will feed nothing else," + 
            "it will feed my revenge. He hath disgraced me, and" + 
            "hindered me half a million; laughed at my losses," + 
            "mocked at my gains, scorned my nation, thwarted my" + 
            "bargains, cooled my friends, heated mine" + 
            "enemies; and what's his reason? I am a Jew. Hath" + 
            "not a Jew eyes? hath not a Jew hands, organs," + 
            "dimensions, senses, affections, passions? fed with" + 
            "the same food, hurt with the same weapons, subject" + 
            "to the same diseases, healed by the same means," + 
            "warmed and cooled by the same winter and summer, as" + 
            "a Christian is? If you prick us, do we not bleed?" + 
            "if you tickle us, do we not laugh? if you poison" + 
            "us, do we not die? and if you wrong us, shall we not" + 
            "revenge? If we are like you in the rest, we will" + 
            "resemble you in that. If a Jew wrong a Christian," + 
            "what is his humility? Revenge. If a Christian" + 
            "wrong a Jew, what should his sufferance be by" + 
            "Christian example? Why, revenge. The villany you" + 
            "teach me, I will execute, and it shall go hard but I" + 
            "will better the instruction.",
            
            "Virtue! a fig! 'tis in ourselves that we are thus" + 
            "or thus. Our bodies are our gardens, to the which" + 
            "our wills are gardeners: so that if we will plant" + 
            "nettles, or sow lettuce, set hyssop and weed up" + 
            "thyme, supply it with one gender of herbs, or" + 
            "distract it with many, either to have it sterile" + 
            "with idleness, or manured with industry, why, the" + 
            "power and corrigible authority of this lies in our" + 
            "wills. If the balance of our lives had not one" + 
            "scale of reason to poise another of sensuality, the" + 
            "blood and baseness of our natures would conduct us" + 
            "to most preposterous conclusions: but we have" + 
            "reason to cool our raging motions, our carnal" + 
            "stings, our unbitted lusts, whereof I take this that" + 
            "you call love to be a sect or scion.",

            "Blow, winds, and crack your cheeks! rage! blow!" + 
            "You cataracts and hurricanoes, spout" + 
            "Till you have drench'd our steeples, drown'd the cocks!" + 
            "You sulphurous and thought-executing fires," + 
            "Vaunt-couriers to oak-cleaving thunderbolts," + 
            "Singe my white head! And thou, all-shaking thunder," + 
            "Smite flat the thick rotundity o' the world!" + 
            "Crack nature's moulds, an germens spill at once," + 
            "That make ingrateful man!"
    };
}
