package io.mosip.authentication.core.indauth.dto;

import java.util.EnumMap;
import java.util.Optional;
import java.util.stream.Stream;

import javax.xml.bind.annotation.XmlValue;

import org.springframework.core.env.Environment;

import com.fasterxml.jackson.annotation.JsonValue;

import io.mosip.authentication.core.constant.IdAuthConfigKeyConstants;

/**
 * General-purpose annotation used for configuring details of user
 * identification.
 * 
 * @author Rakesh Roshan
 * @author Loganathan Sekar
 */
public enum IdType {

	UIN("UIN"), VID("VID"),USER_ID("USERID");

	/**
	 * Value that indicates that default id.
	 */
	public static final IdType DEFAULT_ID_TYPE = IdType.UIN;

	private String type;
	
	private static EnumMap<IdType, String> aliasesMap = new EnumMap<>(IdType.class);

	/**
	 * construct enum with id-type.
	 * 
	 * @param type id type
	 */
	private IdType(String type) {
		this.type = type;
	}

	/**
	 * get id-type.
	 * 
	 * @return type
	 */
	@JsonValue
	@XmlValue
	public String getType() {
		return type;
	}

	@Override
	public String toString() {
		return getType();
	}

	/**
	 * Look for id type or alias either "UIN" or "VID" or "USERID". default id is "UIN"
	 * 
	 * @param typeOrAlias String id-type or alias
	 * @return IDType Optional with IdType
	 */
	public static Optional<IdType> getIDType(String typeOrAlias) {
		if(typeOrAlias == null || typeOrAlias.trim().isEmpty()) {
			return Optional.empty();
		}
		
		return Stream.of(values())
				.filter(t -> t.getType().equalsIgnoreCase(typeOrAlias)
							|| t.getAlias().filter(typeOrAlias::equalsIgnoreCase).isPresent())
				.findAny();

	}
	
	public static IdType getIDTypeOrDefault(String type) {
		return getIDType(type).orElse(DEFAULT_ID_TYPE);

	}
	
	public static String getIDTypeStrOrDefault(String type) {
		return getIDType(type).orElse(DEFAULT_ID_TYPE).getType();
	}
	
	public static String getIDTypeStrOrSameStr(String type) {
		return getIDType(type).map(IdType::getType).orElse(type);
	}
	
	public static void initializeAliases(Environment env) {
		for(IdType idType: IdType.values()) {
			String aliasPropertyKey = String.format(IdAuthConfigKeyConstants.ID_TYPE_ALIAS, idType.getType().toLowerCase());
			String alias = env.getProperty(aliasPropertyKey, "").trim();
			if(!alias.isEmpty()) {
				aliasesMap.put(idType, alias);
			}
		}
	}
	
	public Optional<String> getAlias() {
		return Optional.ofNullable(aliasesMap.get(this));
	}
	
	public String getAliasOrType() {
		return getAlias().orElse(getType());
	}
}
