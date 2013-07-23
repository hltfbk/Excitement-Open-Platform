package eu.excitementproject.eop.common.utilities.linguistics.english.tense;

import java.util.LinkedHashMap;
import java.util.Map;

import eu.excitementproject.eop.common.codeannotations.LanguageDependent;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableMap;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableMapWrapper;


/**
 * 
 * @author Asher Stern
 * @since October 9 2012
 *
 */
@LanguageDependent("english")
public class IrregularTenseTable
{
	public static ImmutableMap<String, EnglishVerbFormsEntity> getTenseMap()
	{
		return tenseMap;
	}
	
	private static final ImmutableMap<String, EnglishVerbFormsEntity> tenseMap;
	static
	{
		Map<String, EnglishVerbFormsEntity> map = new LinkedHashMap<String, EnglishVerbFormsEntity>();
		
		// Taken from http://indodic.com/IrregVerbTenseTable.htm
		
		map.put("abide", new EnglishVerbFormsEntity("abide", "abode, abided", "abode, abided"));
		map.put("arise", new EnglishVerbFormsEntity("arise", "arose", "arisen"));
		map.put("awake", new EnglishVerbFormsEntity("awake", "awoke", "awoken"));
		map.put("be", new EnglishVerbFormsEntity("be", "was", "been"));
		map.put("bear", new EnglishVerbFormsEntity("bear", "bore", "borne, born"));
		map.put("beat", new EnglishVerbFormsEntity("beat", "beat", "beaten"));
		map.put("become", new EnglishVerbFormsEntity("become", "became", "become"));
		map.put("befall", new EnglishVerbFormsEntity("befall", "befell", "befallen"));
		map.put("beget", new EnglishVerbFormsEntity("beget", "begot", "begotten"));
		map.put("begin", new EnglishVerbFormsEntity("begin", "began", "begun"));
		map.put("behold", new EnglishVerbFormsEntity("behold", "beheld", "beheld"));
		map.put("bend", new EnglishVerbFormsEntity("bend", "bent", "bent, bended"));
		map.put("bereave", new EnglishVerbFormsEntity("bereave", "bereaved, bereft", "bereaved, bereft"));
		map.put("beseech", new EnglishVerbFormsEntity("beseech", "besought", "besought"));
		map.put("beset", new EnglishVerbFormsEntity("beset", "beset", "beset"));
		map.put("bet", new EnglishVerbFormsEntity("bet", "bet, betted", "bet, betted"));
		map.put("bid", new EnglishVerbFormsEntity("bid", "bode, bid", "bidden, bid"));
		map.put("bind", new EnglishVerbFormsEntity("bind", "bound", "bound"));
		map.put("bite", new EnglishVerbFormsEntity("bite", "bit", "bitten, bit"));
		map.put("bleed", new EnglishVerbFormsEntity("bleed", "bled", "bled"));
		map.put("blend", new EnglishVerbFormsEntity("blend", "blended", "blended"));
		map.put("bless", new EnglishVerbFormsEntity("bless", "blessed, blest", "blessed, blest"));
		map.put("blow", new EnglishVerbFormsEntity("blow", "blew", "blown"));
		map.put("break", new EnglishVerbFormsEntity("break", "broke", "broken"));
		map.put("breed", new EnglishVerbFormsEntity("breed", "bred", "bred"));
		map.put("bring", new EnglishVerbFormsEntity("bring", "brought", "brought"));
		map.put("broadcast", new EnglishVerbFormsEntity("broadcast", "broadcast", "broadcast"));
		map.put("build", new EnglishVerbFormsEntity("build", "built", "built"));
		map.put("burn", new EnglishVerbFormsEntity("burn", "burnt, burned", "burnt, burned"));
		map.put("burst", new EnglishVerbFormsEntity("burst", "burst", "burst"));
		map.put("buy", new EnglishVerbFormsEntity("buy", "bought", "bought"));
		map.put("cast", new EnglishVerbFormsEntity("cast", "cast", "cast"));
		map.put("catch", new EnglishVerbFormsEntity("catch", "caught", "caught"));
		map.put("choose", new EnglishVerbFormsEntity("choose", "chose", "chosen"));
		map.put("cleave", new EnglishVerbFormsEntity("cleave", "clove, cleft", "cloven, cleft"));
		map.put("cling", new EnglishVerbFormsEntity("cling", "clung", "clung"));
		map.put("clothe", new EnglishVerbFormsEntity("clothe", "clothed", "clothed"));
		map.put("come", new EnglishVerbFormsEntity("come", "came", "come"));
		map.put("cost", new EnglishVerbFormsEntity("cost", "cost", "cost"));
		map.put("creep", new EnglishVerbFormsEntity("creep", "crept", "crept"));
		map.put("crow", new EnglishVerbFormsEntity("crow", "crowed", "crowed"));
		map.put("cut", new EnglishVerbFormsEntity("cut", "cut", "cut"));
		map.put("dare", new EnglishVerbFormsEntity("dare", "dared", "dared"));
		map.put("deal", new EnglishVerbFormsEntity("deal", "dealt", "dealt"));
		map.put("dig", new EnglishVerbFormsEntity("dig", "dug", "dug"));
		map.put("do", new EnglishVerbFormsEntity("do", "did", "done"));
		map.put("draw", new EnglishVerbFormsEntity("draw", "drew", "drawn"));
		map.put("dream", new EnglishVerbFormsEntity("dream", "dreamed, dreamt", "dreamed, dreamt"));
		map.put("drink", new EnglishVerbFormsEntity("drink", "drank", "drunk"));
		map.put("drive", new EnglishVerbFormsEntity("drive", "drove", "driven"));
		map.put("dwell", new EnglishVerbFormsEntity("dwell", "dwelt", "dwelt"));
		map.put("eat", new EnglishVerbFormsEntity("eat", "ate", "eaten"));
		map.put("fall", new EnglishVerbFormsEntity("fall", "fell", "fallen"));
		map.put("feed", new EnglishVerbFormsEntity("feed", "fed", "fed"));
		map.put("feel", new EnglishVerbFormsEntity("feel", "felt", "felt"));
		map.put("fight", new EnglishVerbFormsEntity("fight", "fought", "fought"));
		map.put("find", new EnglishVerbFormsEntity("find", "found", "found"));
		map.put("flee", new EnglishVerbFormsEntity("flee", "fled", "fled"));
		map.put("fling", new EnglishVerbFormsEntity("fling", "flung", "flung"));
		map.put("fly", new EnglishVerbFormsEntity("fly", "flew", "flown"));
		map.put("forbear", new EnglishVerbFormsEntity("forbear", "forbore", "forborne"));
		map.put("forbid", new EnglishVerbFormsEntity("forbid", "forbade, forbad", "forbidden"));
		map.put("forecast", new EnglishVerbFormsEntity("forecast", "forecast, forecasted", "forecast, forecasted"));
		map.put("foresee", new EnglishVerbFormsEntity("foresee", "foresaw", "foreseen"));
		map.put("foretell", new EnglishVerbFormsEntity("foretell", "foretold", "foretold"));
		map.put("forget", new EnglishVerbFormsEntity("forget", "forgot", "forgotten"));
		map.put("forgive", new EnglishVerbFormsEntity("forgive", "forgave", "forgiven"));
		map.put("forsake", new EnglishVerbFormsEntity("forsake", "forsook", "forsaken"));
		map.put("forswear", new EnglishVerbFormsEntity("forswear", "forswore", "forsworn"));
		map.put("freeze", new EnglishVerbFormsEntity("freeze", "froze", "frozen"));
		map.put("get", new EnglishVerbFormsEntity("get", "got", "got, gotten"));
		map.put("gild", new EnglishVerbFormsEntity("gild", "gilded, gilt", "gilded"));
		map.put("give", new EnglishVerbFormsEntity("give", "gave", "given"));
		map.put("go", new EnglishVerbFormsEntity("go", "went", "gone"));
		map.put("grind", new EnglishVerbFormsEntity("grind", "ground", "ground"));
		map.put("grow", new EnglishVerbFormsEntity("grow", "grew", "grown"));
		map.put("hamstring", new EnglishVerbFormsEntity("hamstring", "hamstrung", "hamstrung"));
		map.put("hang", new EnglishVerbFormsEntity("hang", "hung, hanged", "hung, hanged"));
		map.put("have", new EnglishVerbFormsEntity("have", "had", "had"));
		map.put("hear", new EnglishVerbFormsEntity("hear", "heard", "heard"));
		map.put("heave", new EnglishVerbFormsEntity("heave", "heaved, hove", "heaved, hove"));
		map.put("hew", new EnglishVerbFormsEntity("hew", "hewed", "hewed, hewn"));
		map.put("hide", new EnglishVerbFormsEntity("hide", "hid", "hidden, hid"));
		map.put("hit", new EnglishVerbFormsEntity("hit", "hit", "hit"));
		map.put("hold", new EnglishVerbFormsEntity("hold", "hold", "held"));
		map.put("hurt", new EnglishVerbFormsEntity("hurt", "hurt", "hurt"));
		map.put("inlay", new EnglishVerbFormsEntity("inlay", "inlaid", "inlaid"));
		map.put("keep", new EnglishVerbFormsEntity("keep", "kept", "kept"));
		map.put("kneel", new EnglishVerbFormsEntity("kneel", "knelt", "knelt"));
		map.put("knit", new EnglishVerbFormsEntity("knit", "knitted, knit", "knitted, knit"));
		map.put("know", new EnglishVerbFormsEntity("know", "knew", "known"));
		map.put("lay", new EnglishVerbFormsEntity("lay", "laid", "laid"));
		map.put("lead", new EnglishVerbFormsEntity("lead", "led", "led"));
		map.put("learn", new EnglishVerbFormsEntity("learn", "learnt, learned", "learnt, learned"));
		map.put("leave", new EnglishVerbFormsEntity("leave", "left", "left"));
		map.put("lend", new EnglishVerbFormsEntity("lend", "lent", "lent"));
		map.put("let", new EnglishVerbFormsEntity("let", "let", "let"));
		map.put("lie", new EnglishVerbFormsEntity("lie", "lay", "lain"));
		map.put("light", new EnglishVerbFormsEntity("light", "lighted, lit", "lighted, lit"));
		map.put("lose", new EnglishVerbFormsEntity("lose", "lost", "lost"));
		map.put("make", new EnglishVerbFormsEntity("make", "made", "made"));
		map.put("mean", new EnglishVerbFormsEntity("mean", "meant", "meant"));
		map.put("meet", new EnglishVerbFormsEntity("meet", "met", "met"));
		map.put("melt", new EnglishVerbFormsEntity("melt", "melted", "melted, molten"));
		map.put("mislay", new EnglishVerbFormsEntity("mislay", "mislaid", "mislaid"));
		map.put("mislead", new EnglishVerbFormsEntity("mislead", "misled", "misled"));
		map.put("misspell", new EnglishVerbFormsEntity("misspell", "misspell", "misspell"));
		map.put("mistake", new EnglishVerbFormsEntity("mistake", "mistook", "mistaken"));
		map.put("misunderstand", new EnglishVerbFormsEntity("misunderstand", "misunderstood", "misunderstood"));
		map.put("mow", new EnglishVerbFormsEntity("mow", "mowed", "mown, mowed"));
		map.put("outdo", new EnglishVerbFormsEntity("outdo", "outdid", "outdone"));
		map.put("overcome", new EnglishVerbFormsEntity("overcome", "overcame", "overcome"));
		map.put("overdo", new EnglishVerbFormsEntity("overdo", "overdid", "overdone"));
		map.put("overhang", new EnglishVerbFormsEntity("overhang", "overhung", "overhung"));
		map.put("overhear", new EnglishVerbFormsEntity("overhear", "overheard", "overheard"));
		map.put("overlay", new EnglishVerbFormsEntity("overlay", "overlaid", "overlaid"));
		map.put("override", new EnglishVerbFormsEntity("override", "overrode", "overridden"));
		map.put("overrun", new EnglishVerbFormsEntity("overrun", "overran", "overrun"));
		map.put("oversee", new EnglishVerbFormsEntity("oversee", "oversaw", "overseen"));
		map.put("overshoot", new EnglishVerbFormsEntity("overshoot", "overshot", "overshot"));
		map.put("oversleep", new EnglishVerbFormsEntity("oversleep", "overslept", "overslept"));
		map.put("overtake", new EnglishVerbFormsEntity("overtake", "overtook", "overtaken"));
		map.put("overthrow", new EnglishVerbFormsEntity("overthrow", "overthrew", "overthrown"));
		map.put("overwork", new EnglishVerbFormsEntity("overwork", "overworked", "overworked"));
		map.put("partake", new EnglishVerbFormsEntity("partake", "partook", "partaken"));
		map.put("pay", new EnglishVerbFormsEntity("pay", "paid", "paid"));
		map.put("prove", new EnglishVerbFormsEntity("prove", "proved", "proved, proven"));
		map.put("put", new EnglishVerbFormsEntity("put", "put", "put"));
		map.put("read", new EnglishVerbFormsEntity("read", "read", "read"));
		map.put("rebuild", new EnglishVerbFormsEntity("rebuild", "rebuilt", "rebuilt"));
		map.put("recast", new EnglishVerbFormsEntity("recast", "recast", "recast"));
		map.put("redo", new EnglishVerbFormsEntity("redo", "redid", "redone"));
		map.put("relay", new EnglishVerbFormsEntity("relay", "relayed", "relayed"));
		map.put("remake", new EnglishVerbFormsEntity("remake", "remade", "remade"));
		map.put("rend", new EnglishVerbFormsEntity("rend", "rent", "rem"));
		map.put("repay", new EnglishVerbFormsEntity("repay", "repaid", "repaid"));
		map.put("rerun", new EnglishVerbFormsEntity("rerun", "reran", "rerun"));
		map.put("reset", new EnglishVerbFormsEntity("reset", "reset", "reset"));
		map.put("retell", new EnglishVerbFormsEntity("retell", "retold", "retold"));
		map.put("rewrite", new EnglishVerbFormsEntity("rewrite", "rewrote", "rewritten"));
		map.put("rid", new EnglishVerbFormsEntity("rid", "rid, ridden", "rid, ridden"));
		map.put("ride", new EnglishVerbFormsEntity("ride", "rode", "ridden"));
		map.put("ring", new EnglishVerbFormsEntity("ring", "rang", "rung"));
		map.put("rise", new EnglishVerbFormsEntity("rise", "rose", "risen"));
		map.put("run", new EnglishVerbFormsEntity("run", "ran", "run"));
		map.put("saw", new EnglishVerbFormsEntity("saw", "sawed", "sawn (sawed)"));
		map.put("say", new EnglishVerbFormsEntity("say", "said", "said"));
		map.put("see", new EnglishVerbFormsEntity("see", "saw", "seen"));
		map.put("seek", new EnglishVerbFormsEntity("seek", "sought", "sought"));
		map.put("sell", new EnglishVerbFormsEntity("sell", "sold", "sold"));
		map.put("send", new EnglishVerbFormsEntity("send", "sent", "sent"));
		map.put("set", new EnglishVerbFormsEntity("set", "set", "set"));
		map.put("sew", new EnglishVerbFormsEntity("sew", "sewed", "sewn"));
		map.put("shake", new EnglishVerbFormsEntity("shake", "shook", "shaken"));
		map.put("shave", new EnglishVerbFormsEntity("shave", "shaved", "shaved, shaven"));
		map.put("shear", new EnglishVerbFormsEntity("shear", "sheared", "shorn, sheared"));
		map.put("shed", new EnglishVerbFormsEntity("shed", "shed", "shed"));
		map.put("shine", new EnglishVerbFormsEntity("shine", "shone", "shone"));
		map.put("shoe", new EnglishVerbFormsEntity("shoe", "shod", "shod"));
		map.put("shoot", new EnglishVerbFormsEntity("shoot", "shot", "shot"));
		map.put("show", new EnglishVerbFormsEntity("show", "showed", "shown, showed"));
		map.put("shred", new EnglishVerbFormsEntity("shred", "shredded", "shredded"));
		map.put("shrink", new EnglishVerbFormsEntity("shrink", "shrank, shrunk", "shrunk, shrunken"));
		map.put("shut", new EnglishVerbFormsEntity("shut", "shut", "shut"));
		map.put("sing", new EnglishVerbFormsEntity("sing", "sang", "sung"));
		map.put("sink", new EnglishVerbFormsEntity("sink", "sank", "sunk, sunken"));
		map.put("sit", new EnglishVerbFormsEntity("sit", "sat", "sat"));
		map.put("slay", new EnglishVerbFormsEntity("slay", "slew", "slain"));
		map.put("sleep", new EnglishVerbFormsEntity("sleep", "slept", "slept"));
		map.put("slide", new EnglishVerbFormsEntity("slide", "slid", "slid, slidden"));
		map.put("sling", new EnglishVerbFormsEntity("sling", "slung", "slung"));
		map.put("slink", new EnglishVerbFormsEntity("slink", "slunk", "slunk"));
		map.put("slit", new EnglishVerbFormsEntity("slit", "slit", "slit"));
		map.put("smell", new EnglishVerbFormsEntity("smell", "smelt", "smelt"));
		map.put("smite", new EnglishVerbFormsEntity("smite", "smote", "smitten"));
		map.put("sow", new EnglishVerbFormsEntity("sow", "sowed", "sown, sowed"));
		map.put("speak", new EnglishVerbFormsEntity("speak", "spoke", "spoken"));
		map.put("speed", new EnglishVerbFormsEntity("speed", "sped, speeded", "sped, speeded"));
		map.put("spell", new EnglishVerbFormsEntity("spell", "spelt, spelled", "spelt, spelled"));
		map.put("spend", new EnglishVerbFormsEntity("spend", "spent", "spent"));
		map.put("spill", new EnglishVerbFormsEntity("spill", "spilt. spilled", "spilt, spilled"));
		map.put("spin", new EnglishVerbFormsEntity("spin", "spun, span", "spun"));
		map.put("spit", new EnglishVerbFormsEntity("spit", "spat", "spat"));
		map.put("split", new EnglishVerbFormsEntity("split", "split", "split"));
		map.put("spoil", new EnglishVerbFormsEntity("spoil", "spoilt, spoiled", "spoilt; spoiled"));
		map.put("spread", new EnglishVerbFormsEntity("spread", "spread", "spread"));
		map.put("spring", new EnglishVerbFormsEntity("spring", "sprang", "sprung"));
		map.put("stand", new EnglishVerbFormsEntity("stand", "stood", "stood"));
		map.put("steal", new EnglishVerbFormsEntity("steal", "stole", "stolen"));
		map.put("stick", new EnglishVerbFormsEntity("stick", "stuck", "stuck"));
		map.put("sting", new EnglishVerbFormsEntity("sting", "stung", "stung"));
		map.put("stink", new EnglishVerbFormsEntity("stink", "stank, stunk", "stunk"));
		map.put("strew", new EnglishVerbFormsEntity("strew", "strewed", "strewn, strewed"));
		map.put("stride", new EnglishVerbFormsEntity("stride", "strode", "stridden"));
		map.put("strike", new EnglishVerbFormsEntity("strike", "struck", "struck, stricken"));
		map.put("string", new EnglishVerbFormsEntity("string", "strung", "strung"));
		map.put("strive", new EnglishVerbFormsEntity("strive", "strove", "striven."));
		map.put("swear", new EnglishVerbFormsEntity("swear", "swore", "sworn"));
		map.put("sweep", new EnglishVerbFormsEntity("sweep", "swept", "swept"));
		map.put("swell", new EnglishVerbFormsEntity("swell", "swelled", "swollen, swelled"));
		map.put("swim", new EnglishVerbFormsEntity("swim", "swam", "swum"));
		map.put("swing", new EnglishVerbFormsEntity("swing", "swung", "swung"));
		map.put("take", new EnglishVerbFormsEntity("take", "took", "taken"));
		map.put("teach", new EnglishVerbFormsEntity("teach", "taught", "taught"));
		map.put("tear", new EnglishVerbFormsEntity("tear", "tore", "torn"));
		map.put("tell", new EnglishVerbFormsEntity("tell", "told", "told"));
		map.put("think", new EnglishVerbFormsEntity("think", "thought", "thought"));
		map.put("thrive", new EnglishVerbFormsEntity("thrive", "throve, thrived", "thriven, thrived"));
		map.put("throw", new EnglishVerbFormsEntity("throw", "threw", "thrown"));
		map.put("thrust", new EnglishVerbFormsEntity("thrust", "thrust", "thrust"));
		map.put("tread", new EnglishVerbFormsEntity("tread", "trod", "trodden"));
		map.put("understand", new EnglishVerbFormsEntity("understand", "understood", "understood"));
		map.put("undertake", new EnglishVerbFormsEntity("undertake", "undertook", "undertaken"));
		map.put("undo", new EnglishVerbFormsEntity("undo", "undid", "undone"));
		map.put("upset", new EnglishVerbFormsEntity("upset", "upset", "upset"));
		map.put("wake", new EnglishVerbFormsEntity("wake", "woke", "woken"));
		map.put("waylay", new EnglishVerbFormsEntity("waylay", "waylaid", "waylaid"));
		map.put("wear", new EnglishVerbFormsEntity("wear", "wore", "worn"));
		map.put("weave", new EnglishVerbFormsEntity("weave", "wove", "woven"));
		map.put("wed", new EnglishVerbFormsEntity("wed", "wedded", "wedded, wed"));
		map.put("weep", new EnglishVerbFormsEntity("weep", "wept", "wept"));
		map.put("win", new EnglishVerbFormsEntity("win", "won", "won"));
		map.put("wind", new EnglishVerbFormsEntity("wind", "winded, wound", "winded, wound"));
		map.put("withdraw", new EnglishVerbFormsEntity("withdraw", "withdrew", "withdrawn"));
		map.put("withhold", new EnglishVerbFormsEntity("withhold", "withheld", "withheld"));
		map.put("withstand", new EnglishVerbFormsEntity("withstand", "withstood", "withstood"));
		map.put("work", new EnglishVerbFormsEntity("work", "worked", "worked"));
		map.put("wring", new EnglishVerbFormsEntity("wring", "wrung", "wrung"));
		map.put("write", new EnglishVerbFormsEntity("write", "wrote", "written"));
		
		// some additions
		map.put("sky", new EnglishVerbFormsEntity("sky", "skied, skyed", "skied, skyed"));
		map.put("zinc", new EnglishVerbFormsEntity("zinc", "zinced, zincked", "zinced, zincked"));
		map.put("spec", new EnglishVerbFormsEntity("spec", "specced, spec\'ed", "specced, spec\'ed"));
		map.put("sync", new EnglishVerbFormsEntity("sync", "syncked, synched", "syncked, synched"));
		map.put("parallel", new EnglishVerbFormsEntity("parallel", "paralleled, parallelled", "paralleled, parallelled"));

		tenseMap = new ImmutableMapWrapper<String, EnglishVerbFormsEntity>(map);
	}
}
