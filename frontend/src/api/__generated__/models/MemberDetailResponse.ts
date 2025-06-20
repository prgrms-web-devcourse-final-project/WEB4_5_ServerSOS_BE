/* tslint:disable */
/* eslint-disable */
/**
 * PickGO API Document
 * No description provided (generated by Openapi Generator https://github.com/openapitools/openapi-generator)
 *
 * The version of the OpenAPI document: 1.0.0
 *
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */

import { mapValues } from "../runtime"
/**
 *
 * @export
 * @interface MemberDetailResponse
 */
export interface MemberDetailResponse {
  /**
   *
   * @type {string}
   * @memberof MemberDetailResponse
   */
  id?: string
  /**
   *
   * @type {string}
   * @memberof MemberDetailResponse
   */
  email?: string
  /**
   *
   * @type {string}
   * @memberof MemberDetailResponse
   */
  nickname?: string
  /**
   *
   * @type {string}
   * @memberof MemberDetailResponse
   */
  authority?: MemberDetailResponseAuthorityEnum
  /**
   *
   * @type {string}
   * @memberof MemberDetailResponse
   */
  profile?: string
  /**
   *
   * @type {string}
   * @memberof MemberDetailResponse
   */
  socialProvider?: MemberDetailResponseSocialProviderEnum
  /**
   *
   * @type {Date}
   * @memberof MemberDetailResponse
   */
  createdAt?: Date
  /**
   *
   * @type {Date}
   * @memberof MemberDetailResponse
   */
  modifiedAt?: Date
}

/**
 * @export
 */
export const MemberDetailResponseAuthorityEnum = {
  RoleAdmin: "ROLE_ADMIN",
  RoleUser: "ROLE_USER",
} as const
export type MemberDetailResponseAuthorityEnum =
  (typeof MemberDetailResponseAuthorityEnum)[keyof typeof MemberDetailResponseAuthorityEnum]

/**
 * @export
 */
export const MemberDetailResponseSocialProviderEnum = {
  None: "NONE",
  Kakao: "KAKAO",
  Google: "GOOGLE",
} as const
export type MemberDetailResponseSocialProviderEnum =
  (typeof MemberDetailResponseSocialProviderEnum)[keyof typeof MemberDetailResponseSocialProviderEnum]

/**
 * Check if a given object implements the MemberDetailResponse interface.
 */
export function instanceOfMemberDetailResponse(
  value: object,
): value is MemberDetailResponse {
  return true
}

export function MemberDetailResponseFromJSON(json: any): MemberDetailResponse {
  return MemberDetailResponseFromJSONTyped(json, false)
}

export function MemberDetailResponseFromJSONTyped(
  json: any,
  ignoreDiscriminator: boolean,
): MemberDetailResponse {
  if (json == null) {
    return json
  }
  return {
    id: json["id"] == null ? undefined : json["id"],
    email: json["email"] == null ? undefined : json["email"],
    nickname: json["nickname"] == null ? undefined : json["nickname"],
    authority: json["authority"] == null ? undefined : json["authority"],
    profile: json["profile"] == null ? undefined : json["profile"],
    socialProvider:
      json["socialProvider"] == null ? undefined : json["socialProvider"],
    createdAt:
      json["createdAt"] == null ? undefined : new Date(json["createdAt"]),
    modifiedAt:
      json["modifiedAt"] == null ? undefined : new Date(json["modifiedAt"]),
  }
}

export function MemberDetailResponseToJSON(json: any): MemberDetailResponse {
  return MemberDetailResponseToJSONTyped(json, false)
}

export function MemberDetailResponseToJSONTyped(
  value?: MemberDetailResponse | null,
  ignoreDiscriminator: boolean = false,
): any {
  if (value == null) {
    return value
  }

  return {
    id: value["id"],
    email: value["email"],
    nickname: value["nickname"],
    authority: value["authority"],
    profile: value["profile"],
    socialProvider: value["socialProvider"],
    createdAt:
      value["createdAt"] == null ? undefined : value["createdAt"].toISOString(),
    modifiedAt:
      value["modifiedAt"] == null
        ? undefined
        : value["modifiedAt"].toISOString(),
  }
}
