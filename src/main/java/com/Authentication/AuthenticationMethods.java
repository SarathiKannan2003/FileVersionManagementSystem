package com.Authentication;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class AuthenticationMethods {
	 public static String getTimeFormatFromUnixTime(long unixTime,String userTimeZone) {
	    	
	        // Step 1: Convert Unix timestamp to Instant
	        Instant instant = Instant.ofEpochMilli(unixTime);

	        // Step 2: Convert Instant to ZonedDateTime (for specific timezone, like UTC)
	        ZoneId zoneId=ZoneId.of(userTimeZone);
	        ZonedDateTime zonedDateTime = instant.atZone(zoneId);

	        // Step 3: Format ZonedDateTime to a readable string
	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	        String formattedDate = zonedDateTime.format(formatter);
	       
	        return formattedDate;
	}
	 
	 public static String hashPassword(String password) {
			try {
		        // Create MessageDigest instance for SHA-256
		        MessageDigest digest = MessageDigest.getInstance("SHA-256");
		        
		        // Apply SHA-256 hashing to the password (as bytes)
		        byte[] hashBytes = digest.digest(password.getBytes());
		        
		        // Convert byte array into hexadecimal format 
		        StringBuilder hexString = new StringBuilder();
		        for (byte b : hashBytes) {
		            String hex = Integer.toHexString(0xff & b); //0xff:11111111
		            if (hex.length() == 1) {
		                hexString.append('0');
		            }
		            hexString.append(hex);
		        }
		        return hexString.toString();
		    } catch (NoSuchAlgorithmException e) {
		        throw new RuntimeException("Error hashing password", e);
		    }
			}
}
