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

import { mapValues } from '../runtime';
/**
 * 
 * @export
 * @interface SessionCookieConfig
 */
export interface SessionCookieConfig {
    /**
     * 
     * @type {number}
     * @memberof SessionCookieConfig
     */
    maxAge?: number;
    /**
     * 
     * @type {string}
     * @memberof SessionCookieConfig
     */
    domain?: string;
    /**
     * 
     * @type {boolean}
     * @memberof SessionCookieConfig
     */
    httpOnly?: boolean;
    /**
     * 
     * @type {string}
     * @memberof SessionCookieConfig
     */
    path?: string;
    /**
     * 
     * @type {boolean}
     * @memberof SessionCookieConfig
     */
    secure?: boolean;
    /**
     * 
     * @type {string}
     * @memberof SessionCookieConfig
     */
    name?: string;
    /**
     * 
     * @type {{ [key: string]: string; }}
     * @memberof SessionCookieConfig
     */
    attributes?: { [key: string]: string; };
    /**
     * 
     * @type {string}
     * @memberof SessionCookieConfig
     * @deprecated
     */
    comment?: string;
}

/**
 * Check if a given object implements the SessionCookieConfig interface.
 */
export function instanceOfSessionCookieConfig(value: object): value is SessionCookieConfig {
    return true;
}

export function SessionCookieConfigFromJSON(json: any): SessionCookieConfig {
    return SessionCookieConfigFromJSONTyped(json, false);
}

export function SessionCookieConfigFromJSONTyped(json: any, ignoreDiscriminator: boolean): SessionCookieConfig {
    if (json == null) {
        return json;
    }
    return {
        
        'maxAge': json['maxAge'] == null ? undefined : json['maxAge'],
        'domain': json['domain'] == null ? undefined : json['domain'],
        'httpOnly': json['httpOnly'] == null ? undefined : json['httpOnly'],
        'path': json['path'] == null ? undefined : json['path'],
        'secure': json['secure'] == null ? undefined : json['secure'],
        'name': json['name'] == null ? undefined : json['name'],
        'attributes': json['attributes'] == null ? undefined : json['attributes'],
        'comment': json['comment'] == null ? undefined : json['comment'],
    };
}

export function SessionCookieConfigToJSON(json: any): SessionCookieConfig {
    return SessionCookieConfigToJSONTyped(json, false);
}

export function SessionCookieConfigToJSONTyped(value?: SessionCookieConfig | null, ignoreDiscriminator: boolean = false): any {
    if (value == null) {
        return value;
    }

    return {
        
        'maxAge': value['maxAge'],
        'domain': value['domain'],
        'httpOnly': value['httpOnly'],
        'path': value['path'],
        'secure': value['secure'],
        'name': value['name'],
        'attributes': value['attributes'],
        'comment': value['comment'],
    };
}

