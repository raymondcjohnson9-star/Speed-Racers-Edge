package com.speedracersedge.game

/**
 * Manages the party system for Speed Racers Edge.
 *
 * Current version:
 * - Maximum 4 players
 * - Party leader
 * - Any party member can invite friends
 * - Invitation sender name
 * - Invitation sender profile
 * - Accept invitations
 * - Decline invitations
 * - Cancel invitations
 * - Remove party members
 * - Ready / not-ready status
 *
 * Online synchronization will be added later.
 */
class PartyManager {

    companion object {
        const val MAX_PARTY_SIZE = 4
    }

    // ============================================================
    // PLAYER
    // ============================================================

    data class Player(
        val id: String,
        val name: String,
        val profileId: String = "",
        val isLeader: Boolean = false,
        var isReady: Boolean = false
    )

    // ============================================================
    // PARTY INVITATION
    // ============================================================

    data class PartyInvite(
        val inviteId: String,

        // Person sending the invitation
        val fromPlayerId: String,
        val fromPlayerName: String,
        val fromProfileId: String,

        // Person receiving the invitation
        val toPlayerId: String,
        val toPlayerName: String,
        val toProfileId: String
    )

    private val partyMembers =
        mutableListOf<Player>()

    private val pendingInvites =
        mutableListOf<PartyInvite>()

    private var inviteCounter = 1

    // ============================================================
    // LOCAL PLAYER
    // ============================================================

    private val localPlayerId =
        "player_1"

    private val localPlayerName =
        "You"

    private val localProfileId =
        "profile_default"

    init {

        // The local player starts as
        // the party leader.

        partyMembers.add(
            Player(
                id = localPlayerId,
                name = localPlayerName,
                profileId = localProfileId,
                isLeader = true,
                isReady = false
            )
        )
    }

    // ============================================================
    // PARTY INFORMATION
    // ============================================================

    fun getPartyMembers(): List<Player> {
        return partyMembers.toList()
    }

    fun getPartySize(): Int {
        return partyMembers.size
    }

    fun getAvailableSlots(): Int {
        return MAX_PARTY_SIZE -
            partyMembers.size
    }

    fun isPartyFull(): Boolean {
        return partyMembers.size >=
            MAX_PARTY_SIZE
    }

    fun isPlayerInParty(
        playerId: String
    ): Boolean {

        return partyMembers.any {
            it.id == playerId
        }
    }

    fun getPlayer(
        playerId: String
    ): Player? {

        return partyMembers.find {
            it.id == playerId
        }
    }

    fun getLocalPlayer(): Player? {

        return getPlayer(
            localPlayerId
        )
    }

    // ============================================================
    // INVITATIONS
    // ============================================================

    /**
     * Sends an invitation from a party member.
     *
     * Any player already inside the party
     * can send an invitation.
     */
    fun sendInvite(
        fromPlayerId: String,
        toPlayerId: String,
        toPlayerName: String,
        toProfileId: String = ""
    ): PartyInvite? {

        // The sender must actually
        // be inside the party.

        val sender =
            partyMembers.find {
                it.id == fromPlayerId
            } ?: return null

        // Cannot invite someone already
        // inside the party.

        if (isPlayerInParty(toPlayerId)) {
            return null
        }

        // Cannot invite when party is full.

        if (isPartyFull()) {
            return null
        }

        // Do not create duplicate
        // pending invitations.

        val alreadyInvited =
            pendingInvites.any {

                it.toPlayerId == toPlayerId
            }

        if (alreadyInvited) {
            return null
        }

        val invite =
            PartyInvite(

                inviteId =
                    "invite_${inviteCounter++}",

                fromPlayerId =
                    sender.id,

                fromPlayerName =
                    sender.name,

                fromProfileId =
                    sender.profileId,

                toPlayerId =
                    toPlayerId,

                toPlayerName =
                    toPlayerName,

                toProfileId =
                    toProfileId
            )

        pendingInvites.add(
            invite
        )

        return invite
    }

    // ============================================================
    // SIMPLE INVITE HELPER
    // ============================================================

    /**
     * Sends an invitation from the
     * current local player.
     *
     * Useful for the lobby UI.
     */
    fun sendInvite(
        playerId: String,
        playerName: String,
        profileId: String = ""
    ): PartyInvite? {

        return sendInvite(
            fromPlayerId = localPlayerId,
            toPlayerId = playerId,
            toPlayerName = playerName,
            toProfileId = profileId
        )
    }

    // ============================================================
    // PENDING INVITATIONS
    // ============================================================

    fun getPendingInvites(): List<PartyInvite> {
        return pendingInvites.toList()
    }

    fun getInvite(
        inviteId: String
    ): PartyInvite? {

        return pendingInvites.find {
            it.inviteId == inviteId
        }
    }

    fun hasPendingInvite(
        playerId: String
    ): Boolean {

        return pendingInvites.any {
            it.toPlayerId == playerId
        }
    }

    // ============================================================
    // ACCEPT INVITE
    // ============================================================

    /**
     * Accepts an invitation.
     *
     * The invited player joins
     * the sender's party.
     */
    fun acceptInvite(
        inviteId: String
    ): Boolean {

        val invite =
            pendingInvites.find {
                it.inviteId == inviteId
            } ?: return false

        // Party may have become full
        // while invitation was waiting.

        if (isPartyFull()) {

            pendingInvites.remove(
                invite
            )

            return false
        }

        // Prevent duplicate members.

        if (
            isPlayerInParty(
                invite.toPlayerId
            )
        ) {

            pendingInvites.remove(
                invite
            )

            return false
        }

        val newPlayer =
            Player(

                id =
                    invite.toPlayerId,

                name =
                    invite.toPlayerName,

                profileId =
                    invite.toProfileId,

                isLeader = false,

                isReady = false
            )

        partyMembers.add(
            newPlayer
        )

        pendingInvites.remove(
            invite
        )

        return true
    }

    // ============================================================
    // DECLINE INVITE
    // ============================================================

    fun declineInvite(
        inviteId: String
    ): Boolean {

        val invite =
            pendingInvites.find {
                it.inviteId == inviteId
            } ?: return false

        pendingInvites.remove(
            invite
        )

        return true
    }

    // ============================================================
    // CANCEL INVITE
    // ============================================================

    fun cancelInvite(
        inviteId: String
    ): Boolean {

        val invite =
            pendingInvites.find {
                it.inviteId == inviteId
            } ?: return false

        pendingInvites.remove(
            invite
        )

        return true
    }

    // ============================================================
    // REMOVE PLAYER
    // ============================================================

    /**
     * Removes a player from the party.
     *
     * The leader cannot be removed.
     */
    fun removePlayer(
        playerId: String
    ): Boolean {

        val player =
            partyMembers.find {
                it.id == playerId
            } ?: return false

        if (player.isLeader) {
            return false
        }

        partyMembers.remove(
            player
        )

        return true
    }

    // ============================================================
    // READY SYSTEM
    // ============================================================

    fun setReady(
        playerId: String,
        ready: Boolean
    ): Boolean {

        val player =
            partyMembers.find {
                it.id == playerId
            } ?: return false

        player.isReady = ready

        return true
    }

    fun isPlayerReady(
        playerId: String
    ): Boolean {

        return partyMembers
            .find {
                it.id == playerId
            }
            ?.isReady == true
    }

    fun areAllPlayersReady(): Boolean {

        if (
            partyMembers.isEmpty()
        ) {
            return false
        }

        return partyMembers.all {
            it.isReady
        }
    }

    fun getReadyPlayerCount(): Int {

        return partyMembers.count {
            it.isReady
        }
    }

    // ============================================================
    // MATCH START
    // ============================================================

    fun canStartMatch(): Boolean {

        val leaderExists =
            partyMembers.any {
                it.isLeader
            }

        if (!leaderExists) {
            return false
        }

        return areAllPlayersReady()
    }

    // ============================================================
    // LEAVE PARTY
    // ============================================================

    fun leaveParty() {

        val leader =
            partyMembers.find {
                it.isLeader
            }

        partyMembers.clear()

        if (leader != null) {

            leader.isReady = false

            partyMembers.add(
                leader
            )
        }

        pendingInvites.clear()
    }

    // ============================================================
    // RESET
    // ============================================================

    fun reset() {

        partyMembers.clear()

        pendingInvites.clear()

        inviteCounter = 1

        partyMembers.add(
            Player(
                id = localPlayerId,
                name = localPlayerName,
                profileId = localProfileId,
                isLeader = true,
                isReady = false
            )
        )
    }
}