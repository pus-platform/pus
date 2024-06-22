package com.ez.pus.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ez.pus.model.GroupMember;
import com.ez.pus.model.GroupMember.GroupMemberId;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, GroupMemberId> {
    List<GroupMember> findAllByGroupId(Long id);

    List<GroupMember> findAllByUserId(Long userId);

    Optional<GroupMember> findByUserIdAndGroupId(Long userId, Long groupId);
}
