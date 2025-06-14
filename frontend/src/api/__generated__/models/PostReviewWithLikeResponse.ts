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
 * @interface PostReviewWithLikeResponse
 */
export interface PostReviewWithLikeResponse {
  /**
   *
   * @type {number}
   * @memberof PostReviewWithLikeResponse
   */
  reviewId?: number
  /**
   *
   * @type {string}
   * @memberof PostReviewWithLikeResponse
   */
  userId?: string
  /**
   *
   * @type {string}
   * @memberof PostReviewWithLikeResponse
   */
  profile?: string
  /**
   *
   * @type {string}
   * @memberof PostReviewWithLikeResponse
   */
  nickname?: string
  /**
   *
   * @type {string}
   * @memberof PostReviewWithLikeResponse
   */
  content?: string
  /**
   *
   * @type {number}
   * @memberof PostReviewWithLikeResponse
   */
  likeCount?: number
  /**
   *
   * @type {boolean}
   * @memberof PostReviewWithLikeResponse
   */
  likedByCurrentUser?: boolean
}

/**
 * Check if a given object implements the PostReviewWithLikeResponse interface.
 */
export function instanceOfPostReviewWithLikeResponse(
  value: object,
): value is PostReviewWithLikeResponse {
  return true
}

export function PostReviewWithLikeResponseFromJSON(
  json: any,
): PostReviewWithLikeResponse {
  return PostReviewWithLikeResponseFromJSONTyped(json, false)
}

export function PostReviewWithLikeResponseFromJSONTyped(
  json: any,
  ignoreDiscriminator: boolean,
): PostReviewWithLikeResponse {
  if (json == null) {
    return json
  }
  return {
    reviewId: json["reviewId"] == null ? undefined : json["reviewId"],
    userId: json["userId"] == null ? undefined : json["userId"],
    profile: json["profile"] == null ? undefined : json["profile"],
    nickname: json["nickname"] == null ? undefined : json["nickname"],
    content: json["content"] == null ? undefined : json["content"],
    likeCount: json["likeCount"] == null ? undefined : json["likeCount"],
    likedByCurrentUser:
      json["likedByCurrentUser"] == null
        ? undefined
        : json["likedByCurrentUser"],
  }
}

export function PostReviewWithLikeResponseToJSON(
  json: any,
): PostReviewWithLikeResponse {
  return PostReviewWithLikeResponseToJSONTyped(json, false)
}

export function PostReviewWithLikeResponseToJSONTyped(
  value?: PostReviewWithLikeResponse | null,
  ignoreDiscriminator: boolean = false,
): any {
  if (value == null) {
    return value
  }

  return {
    reviewId: value["reviewId"],
    userId: value["userId"],
    profile: value["profile"],
    nickname: value["nickname"],
    content: value["content"],
    likeCount: value["likeCount"],
    likedByCurrentUser: value["likedByCurrentUser"],
  }
}
