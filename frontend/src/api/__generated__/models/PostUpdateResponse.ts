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
import type { PostDetailResponse } from "./PostDetailResponse"
import {
  PostDetailResponseFromJSON,
  PostDetailResponseFromJSONTyped,
  PostDetailResponseToJSON,
  PostDetailResponseToJSONTyped,
} from "./PostDetailResponse"

/**
 *
 * @export
 * @interface PostUpdateResponse
 */
export interface PostUpdateResponse {
  /**
   *
   * @type {PostDetailResponse}
   * @memberof PostUpdateResponse
   */
  post?: PostDetailResponse
}

/**
 * Check if a given object implements the PostUpdateResponse interface.
 */
export function instanceOfPostUpdateResponse(
  value: object,
): value is PostUpdateResponse {
  return true
}

export function PostUpdateResponseFromJSON(json: any): PostUpdateResponse {
  return PostUpdateResponseFromJSONTyped(json, false)
}

export function PostUpdateResponseFromJSONTyped(
  json: any,
  ignoreDiscriminator: boolean,
): PostUpdateResponse {
  if (json == null) {
    return json
  }
  return {
    post:
      json["post"] == null
        ? undefined
        : PostDetailResponseFromJSON(json["post"]),
  }
}

export function PostUpdateResponseToJSON(json: any): PostUpdateResponse {
  return PostUpdateResponseToJSONTyped(json, false)
}

export function PostUpdateResponseToJSONTyped(
  value?: PostUpdateResponse | null,
  ignoreDiscriminator: boolean = false,
): any {
  if (value == null) {
    return value
  }

  return {
    post: PostDetailResponseToJSON(value["post"]),
  }
}
