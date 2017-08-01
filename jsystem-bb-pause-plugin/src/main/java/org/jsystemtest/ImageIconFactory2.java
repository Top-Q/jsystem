package org.jsystemtest;


import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;

import javax.swing.ImageIcon;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;


public class ImageIconFactory2 {

	private enum IconStatusGroup {
		NORMAL("pausedNormal"),
		ERROR("pausedError"),
		WARNING("pausedWarning"),
		SUCCESS("pausedSuccess"),
		FAILURE("pausedFailure");

		private String folderName;

		IconStatusGroup(String folderName) {
			this.folderName = folderName;
		}

		public String getFolderName() {
			return folderName;
		}
	}
	
	
	public enum IconStateGroup {
		PAUSED_NORMAL,
		PAUSED_NEGATIVE,
		PAUSED_ISSUE,
		PAUSED_NEG_ISSUE;
	}
	
	private HashMap<IconStatusGroup,HashMap<String,ImageIcon>> imageIconTypesMap;
	
	public ImageIconFactory2() throws IOException {
		//HashMap<IconGroup,HashMap<String,ImageIcon>> tempIconTypeMap = new HashMap<>();
		imageIconTypesMap = new HashMap<>(); 
		for (IconStatusGroup iconType : IconStatusGroup.values()) {
			
			HashMap<String,ImageIcon> ImageIconsMap = new HashMap<>();
			List<String> files = IOUtils.readLines(
					ImageIconFactory2.class.getClassLoader().getResourceAsStream(iconType.getFolderName()+"/"), Charsets.UTF_8);
			final String iconTypeName = iconType.toString().toLowerCase();
			files.removeIf(new Predicate<String>() {
				@Override
				public boolean test(String fileName) {
					return !fileName.toLowerCase().contains(iconTypeName);
				}
			});
			
			for(String fileName : files){
				ImageIcon tempImageIcon = new ImageIcon(
						IOUtils.toByteArray(ImageIconFactory2.class.getClassLoader().getResourceAsStream(iconType.getFolderName()+"/"+fileName)));
				fileName = fileName.toLowerCase().replace(".gif", "").replace(iconType.toString().toLowerCase(), "");
				ImageIconsMap.put(fileName, tempImageIcon);
			}
			imageIconTypesMap.put(iconType, ImageIconsMap);
		}
	}
	
	

	public HashMap<IconStateGroup,HashMap<String,ImageIcon>> getImageIconTypesMap() {
		HashMap<String,ImageIcon> pausedNormalMap = new HashMap<>();
		HashMap<String,ImageIcon> pausedMarkedNegMap = new HashMap<>();
		HashMap<String,ImageIcon> pausedMarkedIsseuMap = new HashMap<>();
		HashMap<String,ImageIcon> PausedMarkedNegIssueMap = new HashMap<>();
		
		for(IconStatusGroup iconType : IconStatusGroup.values()){
			for(String key : imageIconTypesMap.get(iconType).keySet()){
				if(key.endsWith("issue") && !key.contains("negissue")){
					key = key.replace("issue", "");
					pausedMarkedIsseuMap.put(key+iconType.toString().toLowerCase(), imageIconTypesMap.get(iconType).get(key));
				}
				else if(key.endsWith("neg")){
					key = key.replace("neg", "");
					pausedMarkedNegMap.put(key+iconType.toString().toLowerCase(), imageIconTypesMap.get(iconType).get(key));
				}
				else if(key.endsWith("negissue")){
					key = key.replace("negissue", "");
					PausedMarkedNegIssueMap.put(key+iconType.toString().toLowerCase(), imageIconTypesMap.get(iconType).get(key));
				}
				else pausedNormalMap.put(key+iconType.toString().toLowerCase(), imageIconTypesMap.get(iconType).get(key));
			}
		}
		HashMap<IconStateGroup,HashMap<String,ImageIcon>> concatedMap = new HashMap<>();
		concatedMap.put(IconStateGroup.PAUSED_NORMAL, pausedNormalMap);
		concatedMap.put(IconStateGroup.PAUSED_NEGATIVE, pausedMarkedNegMap);
		concatedMap.put(IconStateGroup.PAUSED_ISSUE, pausedMarkedIsseuMap);
		concatedMap.put(IconStateGroup.PAUSED_NEG_ISSUE, PausedMarkedNegIssueMap);
		return concatedMap;
	}

	
	

	

	

}
